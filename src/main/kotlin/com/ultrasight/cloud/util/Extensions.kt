package com.ultrasight.cloud.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

const val LICENSE_DATE_TIME_FORMAT = "MMM dd, yyy HH:mm:ss"
const val EXPORT_DICOM_DATE_TIME_FORMAT = "yyyyMMddHHmmss.SSSSSS"
const val EXPORT_DICOM_DATE_FORMAT = "yyyyMMdd"
const val UI_BIRTHDATE_FORMAT = "dd/MM/yyyy"

fun now() = System.currentTimeMillis()

fun Long.formatDate(datePattern: String, timeZone: String = "UTC"): String {
    val sdf = SimpleDateFormat(datePattern)
    sdf.timeZone = TimeZone.getTimeZone(timeZone)
    return sdf.format(this)
}

fun String.parseDate(datePattern: String, timeZone: String = "UTC"): Long {
    val sdf = SimpleDateFormat(datePattern)
    sdf.timeZone = TimeZone.getTimeZone(timeZone)
    val date = sdf.parse(this)
    return date.time
}

fun getYearsBetween(olderDate: String, recentDate: String, formatPattern: String): Int {
    val date1: LocalDate = LocalDate.parse(olderDate, DateTimeFormatter.ofPattern(formatPattern))
    val date2: LocalDate = LocalDate.parse(recentDate, DateTimeFormatter.ofPattern(formatPattern))
    val period: Period = date1.until(date2)
    return period.years
}

fun Long.asStartOfDay(zoneOffset: ZoneOffset = ZoneOffset.UTC): Long {
    return Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC).toLocalDate().atStartOfDay(zoneOffset).toInstant().toEpochMilli()
}

fun getFullName(firstName: String?, middleName: String?, lastName: String?): String {
    val first = firstName?:""
    val middle = middleName?:""
    val last = lastName?:""
    return if (middle.isEmpty()) "$first $last".trim()
    else "$first $middle $last".trim()
}

fun parsePatientName(patientName: String?): Triple<String?, String?, String?> {
    if (patientName == null)
        return Triple(null, null, null)
    val names = patientName.split('^')
    val lastName = names.first().ifEmpty { null }
    val firstName = if (names.size > 1) names[1].ifEmpty { null } else null
    val middleName = if (names.size > 2) names[2].ifEmpty { null } else null
    return Triple(firstName, middleName, lastName)
}
