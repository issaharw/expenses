package com.issahar.expenses.util

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun now() = System.currentTimeMillis()

fun Long.formatDate(datePattern: String, timeZone: String = "UTC"): String {
    val sdf = SimpleDateFormat(datePattern)
    sdf.timeZone = TimeZone.getTimeZone(timeZone)
    return sdf.format(this)
}

fun String.parseDate(datePattern: String, timeZone: String = "UTC"): Date {
    val sdf = SimpleDateFormat(datePattern)
    sdf.timeZone = TimeZone.getTimeZone(timeZone)
    return sdf.parse(this)
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

fun getCurrentBudgetMonth(): String {
    val israelZoneId = ZoneId.of("Asia/Jerusalem")
    val todayInIsrael = ZonedDateTime.now(israelZoneId).toLocalDate()
    val adjustedDate = if (todayInIsrael.dayOfMonth >= 10) {
        todayInIsrael
    } else {
        todayInIsrael.minusMonths(1)
    }
    return adjustedDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
}