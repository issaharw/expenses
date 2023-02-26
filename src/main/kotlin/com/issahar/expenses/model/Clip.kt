package com.issahar.expenses.model

import com.ultrasight.cloud.util.mapper

data class Clip(val id: Int,
                val instanceUid: String,
                val examId: Int,
                val clipTime: Long?,
                val clipType: ClipType,
                val status: ClipStatus,
                val imagesJson: List<UiFrame>?,
                val networkResults: NetworkResults?,
                val cineRate: Int,
                val creationTime: Long) {
    val imagesJsonAsString: String
        get() = mapper.writeValueAsString(imagesJson)
    val networkResultsAsString: String
        get() = mapper.writeValueAsString(networkResults)
    val statusCode = status.value
    val clipTypeValue = clipType.value

}
