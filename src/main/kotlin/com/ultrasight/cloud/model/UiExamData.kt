package com.ultrasight.cloud.model

import com.ultrasight.cloud.util.UI_BIRTHDATE_FORMAT
import com.ultrasight.cloud.util.formatDate
import com.ultrasight.cloud.util.getYearsBetween
import com.ultrasight.cloud.util.now

data class UiExamData(
    val studyUid: String,
    val patientFirstName: String?,
    val patientMiddleName: String?,
    val patientLastName: String?,
    val patientGender: String?,
    val patientBirthDate: String?,
    val mrn: String,
    val examTime: String?,
    val isReadyToView: Boolean,
    val numberOfClips: Int = 0,
    val numberOfImages: Int = 0,
    val combinedQS: Int?,
    val networkResults: Map<String, Int>?,
    val doneCalculating: Boolean)
{
    val patientAge = if (patientBirthDate != null)
        "${getYearsBetween(patientBirthDate, now().formatDate(UI_BIRTHDATE_FORMAT), UI_BIRTHDATE_FORMAT)} y"
    else null
}