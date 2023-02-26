package com.issahar.expenses.model

import com.ultrasight.cloud.util.UI_BIRTHDATE_FORMAT
import com.ultrasight.cloud.util.formatDate
import com.ultrasight.cloud.util.getYearsBetween
import com.ultrasight.cloud.util.now

data class ClipFullData(
    val patientId: Int,
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
    val instanceUid: String,
    val clipType: ClipType,
    val clipStatus: ClipStatus,
    val networkResults: NetworkResults? = null,
    val imagesJson: List<UiFrame>?=null,
    val cineRate: Int)
{
    val patientAge = if (patientBirthDate != null)
        "${getYearsBetween(patientBirthDate, now().formatDate(UI_BIRTHDATE_FORMAT), UI_BIRTHDATE_FORMAT)} y"
    else null

    fun getViewQuality(view:String): Double {
        return if (view == networkResults!!.identifiedView)
            (networkResults.clipQuality ?: 0.0) * 100
        else
            0.0
    }
}