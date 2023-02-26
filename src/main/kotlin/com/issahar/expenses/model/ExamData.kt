package com.issahar.expenses.model

import com.issahar.expenses.util.UI_BIRTHDATE_FORMAT
import com.issahar.expenses.util.formatDate
import com.issahar.expenses.util.getYearsBetween
import com.issahar.expenses.util.now

data class ExamData(
    val patientId: Int,
    val patientName: String?,
    val patientFirstName: String?,
    val patientMiddleName: String?,
    val patientLastName: String?,
    val patientGender: String?,
    val patientBirthDate: String?,
    val mrn: String,
    val examId: Int,
    val studyUid: String,
    val examTime: String?,
    val examStatus: Status,
    val clipType: ClipType = ClipType.CLIP,
    val clipStatus: ClipStatus = ClipStatus.PROCESSING,
    val networkResults: NetworkResults? = null,
    val performing: String,
    val attending: String,
    val accessionNumber: String,
    val manufacturerModel: String,
    val transducerData: String,
    val processingFunction: String)
{
    val patientAge = if (patientBirthDate != null)
        "${getYearsBetween(patientBirthDate, now().formatDate(UI_BIRTHDATE_FORMAT), UI_BIRTHDATE_FORMAT)} y"
    else null
}