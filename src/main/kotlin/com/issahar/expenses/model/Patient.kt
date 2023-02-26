package com.issahar.expenses.model

import com.issahar.expenses.util.getFullName

data class Patient(val id: Int,
                   val mrn: String,
                   val firstName: String?,
                   val middleName: String?,
                   val lastName: String?,
                   val gender: String?,
                   val birthDate: Long?,
                   val creationTime: Long)
{
    var patientFullName = getFullName(firstName, middleName, lastName)
}