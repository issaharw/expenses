package com.issahar.expenses.handler

import com.issahar.expenses.dao.ClipDao
import com.issahar.expenses.dao.ExamDao
import com.issahar.expenses.dao.PatientDao
import com.issahar.expenses.di.Async
import com.issahar.expenses.di.Config
import com.issahar.expenses.model.*
import com.issahar.expenses.storage.Storage
import com.issahar.expenses.util.parsePatientName
import com.issahar.expenses.util.httpPost
import com.issahar.expenses.util.now
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO
import jakarta.inject.Inject
import java.awt.image.BufferedImage

private const val IMAGE_FILE_SUFFIX = "jpg"
private const val QUALITY_PROCESSING_PATH = "/api/calculate-clip-quality"
private val viewsForCombinedQS = listOf("PLAX","SAX MV", "2C", "4C", "SC 4C HOLD AIR", "SC IVC")
private val allViews = listOf("2C", "3C", "4C", "5C", "PLAX", "SAX AV", "SAX MV", "SAX PAP", "SC IVC", "SC 4C HOLD AIR")

@Component
class ClipHandler @Inject constructor(private val config: Config,
                                      private val patientDao: PatientDao,
                                      private val examDao: ExamDao,
                                      private val clipDao: ClipDao,
                                      private val storage: Storage) {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    fun handleClip(filePath: String): Clip? {
//        val dcmTempFile = storage.getFileFromTemp(filePath) ?: return null
//        val clipFromDB = clipDao.getClip("")
//        val clip = if (clipFromDB == null) {
//            createClip(dicomData)
//                .also { logger.info("clip ${dicomData.instanceId} was created in the db") }
//        }
//        else {
//            // re-running clip even if it exists
//            updateClipStatusToCreated(clipFromDB)
//                .also { logger.info("clip ${dicomData.instanceId} was saved in db with status 'created' again.") }
//        }
//        Async.pool.execute(ClipProcessor(dicomData, dcmTempFile, clip))
//        return clip
        return null
    }

    fun createClip(dicomData: DicomData): Clip {
        val exam = getOrCreateExam(dicomData)
        val clipType = if (dicomData.numberOfFrames > 1) ClipType.CLIP else ClipType.IMAGE
        val imagesJson = createImagesJson(dicomData.numberOfFrames, dicomData.clipImagesFolder)
        val clip = Clip(0, dicomData.instanceId, exam.id, dicomData.clipTime, clipType, ClipStatus.CREATED, imagesJson, null, dicomData.cineRate, now())
        val clipId = clipDao.addClip(clip)
        return clip.copy(id=clipId)
    }

    fun getOrCreateExam(dicomData: DicomData): Exam {
        var exam = examDao.getExam(dicomData.studyId)
        if (exam == null) {
            val patient = getOrCreatePatient(dicomData)
            val userData = ExamUserData(listOf(), IndicationsData(listOf(), listOf()), UserAddedExamData())
            exam = Exam(0, dicomData.studyId, patient.id, Status.CREATED, dicomData.examTime, userData, dicomData.performing,
                dicomData.attending, dicomData.accessionNumber, dicomData.manufacturerModel, dicomData.transducerData, dicomData.processingFunction, now())
            val examId = examDao.addExam(exam) // error handling
            exam = exam.copy(id=examId)
        }
        return exam
    }

    fun getOrCreatePatient(dicomData: DicomData): Patient {
        var patient = patientDao.getPatientByMrn(dicomData.patientId)
        if (patient == null) {
            val (firstName, middleName, lastName) = parsePatientName(dicomData.patientName)
            patient = Patient(0, dicomData.patientId, firstName, middleName, lastName, dicomData.patientSex, dicomData.birthDate, now())
            // add patient to db
            val patientId = patientDao.addPatient(patient) // error handling?
            patient = patient.copy(id=patientId)
        }
        return patient
    }

    private fun updateClipStatusToCreated(clip: Clip): Clip {
        clipDao.updateClipStatus(clip.id, ClipStatus.CREATED.value)
        return clip.copy(status = ClipStatus.CREATED)
    }

    private fun createImagesJson(numberOfImages: Int, imagesFolder: String) : List<UiFrame> {
        val uiFrames = mutableListOf<UiFrame>()
        for (i in 0 until numberOfImages) {
            uiFrames.add(UiFrame(i.toLong(), "$imagesFolder/$i.$IMAGE_FILE_SUFFIX"))
        }
        return uiFrames
    }

    fun saveClipFileToTemp(filePath: String, inputStream: InputStream): File? {
        val file = File(config.tempFolder, filePath)
        return try {
            file.parentFile.mkdirs()
            inputStream.copyTo(FileOutputStream(file))
            file
        } catch (e: IOException) {
            logger.error("Could not save Dicom file to disk. File: ${file.absolutePath}.", e)
            null
        }
    }


    private fun sendClipToQualityProcessing(clipId: Int, clipPath: String) {
        val start = now()
        logger.info("Sending clip $clipId to AI Server")
        val url = "${config.aiServerHost}$QUALITY_PROCESSING_PATH"
        val json = """{"clip_id": $clipId, "clip_path": "$clipPath"} """
        val response = httpPost(url, json)
        val end = now()
        logger.info("AI Server returned in ${end - start} with response: ${response.body}")
    }

    fun saveQualityResults(clipId: Int, qualityResults: String) {
        clipDao.updateNetworkResultsAndStatus(clipId, qualityResults, ClipStatus.DONE.value)
    }

    fun saveQualityResultsError(clipId: Int) {
        clipDao.updateClipStatus(clipId, ClipStatus.ERROR_CALCULATING_RESULTS.value)
    }

    fun getExamResultsSummary(examClips: List<ClipFullData>): Triple<Map<String, Int>, Int, Boolean> {
        val examWithResults = examClips.filter { it.clipType == ClipType.CLIP && it.networkResults != null }
        if (examWithResults.isEmpty())
            return Triple(mapOf(), 0, false)
        val joinedResults = allViews.associateWith { view ->
            examWithResults.maxOf { it.getViewQuality(view) }.toInt()
        }
        val combinedQS = (joinedResults.filter { it.key in viewsForCombinedQS }.values).average().toInt()
        val doneCalculating = examClips.filter { it.clipType == ClipType.CLIP  }.all { it.clipStatus == ClipStatus.DONE }
        return Triple(joinedResults, combinedQS, doneCalculating)
    }

    fun getExamResultsWithClips(examClips: List<ClipFullData>): Map<String, ViewAnalysisData> {
        val qualityThreshold = 20
        val examWithResults = examClips.filter { it.clipType == ClipType.CLIP && it.networkResults != null }
        if (examWithResults.isEmpty())
            return mapOf()
        val joinedResults = allViews.associateWith { view ->
            val maxClipFullData = examWithResults.maxByOrNull { it.getViewQuality(view) }!!
            val instanceUid = maxClipFullData.instanceUid
            val maxValue = maxClipFullData.getViewQuality(view).toInt()
            val numOfClips = examWithResults.count{ (it.getViewQuality(view)).toInt() > qualityThreshold}
            ViewAnalysisData(instanceUid, maxValue, numOfClips)
        }
        return joinedResults
    }

    fun deleteClips(clipIds: List<Int>) {
        clipDao.deleteClips(clipIds)
    }

    private inner class ClipProcessor(private val dicomData: DicomData, private val dcmTempFile: File, private val clip: Clip): Runnable {
        override fun run() {
            saveClipToStorageAndProcessIt()
        }

        private fun saveClipToStorageAndProcessIt() {
            logger.info("Starting to process clip ${dicomData.instanceId}.")
            val successful = saveDicomImagesToStorage(dicomData, dcmTempFile)
            if (!successful) {
                clipDao.updateClipStatus(clip.id, ClipStatus.ERROR_SAVING_TO_STORAGE.value)
                logger.error("clip ${dicomData.instanceId} failed to save to storage")
                return
            }
            logger.info("clip ${dicomData.instanceId} was saved to storage")
            clipDao.updateClipStatus(clip.id, ClipStatus.PROCESSING.value)
            if (clip.clipType == ClipType.CLIP)
                sendClipToQualityProcessing(clip.id, dicomData.clipFile)
            else
                logger.info("Not sending clip ${clip.id} to network results, as it is an image.")
            dcmTempFile.delete()
        }

        private fun saveDicomImagesToStorage(dicomData: DicomData, dicomFile:File): Boolean {
            logger.info("Saving file to storage. Clip: ${dicomData.instanceId}")
            val dicomUploadSuccess =  storage.saveFile(dicomData.clipFile, dicomFile)
            logger.info("File saved to storage. Clip: ${dicomData.instanceId}. Successful: $dicomUploadSuccess")
            if (!dicomUploadSuccess) return false
            for (index in 0 until dicomData.numberOfFrames) {
                val imageTempFile = File(config.tempFolder, "${dicomData.instanceId}/$index.$IMAGE_FILE_SUFFIX")
                imageTempFile.parentFile.mkdirs()
                val imageUploadSuccess = storage.saveFile("${dicomData.clipImagesFolder}/${imageTempFile.name}", imageTempFile)
                imageTempFile.delete()
                if (!imageUploadSuccess) return false
            }
            return true
        }
    }
}