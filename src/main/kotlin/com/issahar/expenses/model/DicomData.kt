package com.issahar.expenses.model

import com.ultrasight.cloud.util.EXPORT_DICOM_DATE_FORMAT
import com.ultrasight.cloud.util.EXPORT_DICOM_DATE_TIME_FORMAT
import com.ultrasight.cloud.util.parseDate
import org.slf4j.LoggerFactory

private const val IMAGES_FOLDER_NAME = "images"
const val DCM_FILE_SUFFIX = "dcm"

private val logger = LoggerFactory.getLogger(DicomData::class.java)

data class DicomData(val patientId: String,
                     val studyId: String,
                     val instanceId: String,
                     val studyDate: String,
                     val studyTime: String,
                     val patientName: String?,
                     val patientSex: String?,
                     val patientBirthDate: String?,
                     val time: String,
                     val numberOfFrames: Int,
                     val performing: String,
                     val attending: String,
                     val accessionNumber: String,
                     val manufacturerModel: String,
                     val transducerData: String,
                     val processingFunction: String,
                     val cineRate: Int)
{
    private val clipFolder = "$patientId/$studyId/$instanceId"
    val clipFile = "$clipFolder/$instanceId.$DCM_FILE_SUFFIX"
    val clipImagesFolder = "$clipFolder/$IMAGES_FOLDER_NAME"
    val examTime = "$studyDate$studyTime".parseDate(EXPORT_DICOM_DATE_TIME_FORMAT)
    val birthDate = getBirthDate()?.parseDate(EXPORT_DICOM_DATE_FORMAT)
    val clipTime = time.parseDate(EXPORT_DICOM_DATE_TIME_FORMAT)

    private fun getBirthDate(): String? {
        return when {
            patientBirthDate == null -> null
            patientBirthDate.length == 4 -> "${patientBirthDate}0101"
            patientBirthDate.length > 4 -> patientBirthDate
            else ->  {
                logger.error("Patient birth date is very strange: $patientBirthDate")
                null
            }
        }
    }
}
