package com.issahar.expenses.resource

import com.fasterxml.jackson.module.kotlin.readValue
import com.issahar.expenses.dao.ClipDao
import com.issahar.expenses.dao.ExamDao
import com.issahar.expenses.dao.PatientDao
import com.issahar.expenses.handler.ClipHandler
import com.issahar.expenses.model.*
import com.issahar.expenses.storage.Storage
import org.slf4j.LoggerFactory
import com.issahar.expenses.util.*
import java.io.*
import jakarta.inject.Inject
import jakarta.ws.rs.*

@Path("/exams")
class ExamResource @Inject constructor(private val patientDao: PatientDao,
                                       private val examDao: ExamDao,
                                       private val clipDao: ClipDao,
                                       private val storage: Storage,
                                       private val clipHandler: ClipHandler) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GET
    @Path("/{studyUid}/clips")
    @Produces("application/json")
    fun getExamClips(@PathParam("studyUid") studyUid: String): List<ClipFullData> {
        return clipDao.getExamClips(studyUid)
    }

    @GET
    @Path("/")
    @Produces("application/json")
    fun getExams(): List<UiExamData> {
        val allFullClipsData = clipDao.getFullClipDataData()
        val examClips = allFullClipsData.groupBy { it.examId }

        val uiExamsData = examClips.entries.map { (_, clips) ->
            val (joinedResults, combinedQS, doneCalculating) = clipHandler.getExamResultsSummary(clips)
            val exam = clips.first()
            UiExamData(
                exam.studyUid,
                exam.patientFirstName,
                exam.patientMiddleName,
                exam.patientLastName,
                exam.patientGender,
                exam.patientBirthDate,
                exam.mrn,
                exam.examTime,
                clips.all { it.clipStatus == ClipStatus.DONE },
                clips.count { it.clipType == ClipType.CLIP },
                clips.count { it.clipType == ClipType.IMAGE },
                combinedQS,
                joinedResults,
                doneCalculating
            )
        }
        return uiExamsData
    }

    @GET
    @Path("/{studyUid}/clips-data")
    @Produces("application/json")
    fun getExamClipsImages(@PathParam("studyUid") studyUid: String): ExamClipsData {
        val allFullClipsData = clipDao.getExamClips(studyUid)
        val joinedResults = clipHandler.getExamResultsWithClips(allFullClipsData)
        return ExamClipsData(allFullClipsData.map { getClipData(it) }, joinedResults)
    }

    @GET
    @Path("/{studyUid}/exam-data")
    @Produces("application/json")
    fun getExamData(@PathParam("studyUid") studyUid: String): ExamData? {
        val exam = examDao.getExam(studyUid)
        if (exam == null) {
            logger.warn("Exam $studyUid wasn't found")
            return null
        }
        val patient = patientDao.getPatientById(exam.patientId)
        if (patient == null) {
            logger.warn("Patient ${exam.patientId} wasn't found")
            return null
        }
        val examTime = exam.examTime?.formatDate(LICENSE_DATE_TIME_FORMAT)
        val birthDate = patient.birthDate?.formatDate(UI_BIRTHDATE_FORMAT)
        return ExamData(patient.id,
            patient.patientFullName,
            patient.firstName,
            patient.middleName,
            patient.lastName,
            patient.gender,
            birthDate,
            patient.mrn,
            exam.id,
            exam.studyUid,
            examTime,
            exam.status,
            performing = exam.performing,
            attending = exam.attending,
            accessionNumber = exam.accessionNumber,
            manufacturerModel = exam.manufacturerModel,
            transducerData = exam.transducerData,
            processingFunction = exam.processingFunction)
    }

    @GET
    @Path("/{studyUid}/user-data")
    @Produces("application/json")
    fun getUserData(@PathParam("studyUid") studyUid: String): ExamUserData? {
        return examDao.getExam(studyUid)?.userData
    }

    @PUT
    @Path("/{studyUid}/user-data")
    @Consumes("application/json")
    fun updateUserData(@PathParam("studyUid") studyUid: String, userData: String): Boolean {
        try {
            mapper.readValue<ExamUserData>(userData) // Just for validation
            examDao.updateUserData(studyUid, userData)
            return true
        }
        catch (e: Exception) {
            logger.error("User data is in incorrect format. User Data: $userData", e)
            throw e
        }
    }

    @DELETE
    @Path("/all")
    @Produces("application/json")
    fun deleteAllData(): Boolean {
        clipDao.deleteAllClips()
        examDao.deleteAllExams()
        patientDao.deleteAllPatients()
        return true
    }

    private fun getClipData(clipFullData: ClipFullData): ClipViewerData? {
        if (clipFullData.imagesJson == null) {
            logger.error("Clip (${clipFullData.instanceUid}) has no images json!")
            return null
        }
        val images = clipFullData.imagesJson.map { storage.getFileUrl(it.frame_filename) }
        return ClipViewerData(clipFullData.instanceUid,
            images, clipFullData.networkResults,
            clipFullData.cineRate,
            clipFullData.clipStatus.imagesUploadedSuccessfully())
    }
}
