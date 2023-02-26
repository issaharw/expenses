package com.issahar.expenses.model

import com.ultrasight.cloud.util.mapper

data class Exam(val id: Int,
                val studyUid: String,
                val patientId: Int,
                val status: Status,
                val examTime: Long?,
                val userData: ExamUserData?,
                val performing: String,
                val attending: String,
                val accessionNumber: String,
                val manufacturerModel: String,
                val transducerData: String,
                val processingFunction: String,
                val creationTime: Long)
{
    val statusCode = status.value
    val userDataAsString = mapper.writeValueAsString(userData)
}