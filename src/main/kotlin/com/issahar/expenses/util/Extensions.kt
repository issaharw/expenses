package com.issahar.expenses.util

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun now() = System.currentTimeMillis()

fun String.parseDate(datePattern: String, timeZone: String = "UTC"): Date {
    val sdf = SimpleDateFormat(datePattern)
    sdf.timeZone = TimeZone.getTimeZone(timeZone)
    return sdf.parse(this)
}

fun getCurrentBudgetMonth() = Date().localDate().budgetMonth()

fun Date.localDate(): LocalDate = this.toInstant().atZone(ZoneId.of("Asia/Jerusalem")).toLocalDate()

fun LocalDate.budgetMonth(): String {
    val adjustedDate = if (this.dayOfMonth >= 10) {
        this.plusMonths(1)
    } else {
        this
    }
    return adjustedDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
}

fun Date.budgetMonthFromChargeDate() = this.localDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))

//fun Long.formatDate(datePattern: String, timeZone: String = "UTC"): String {
//    val sdf = SimpleDateFormat(datePattern)
//    sdf.timeZone = TimeZone.getTimeZone(timeZone)
//    return sdf.format(this)
//}
//
//fun getYearsBetween(olderDate: String, recentDate: String, formatPattern: String): Int {
//    val date1: LocalDate = LocalDate.parse(olderDate, DateTimeFormatter.ofPattern(formatPattern))
//    val date2: LocalDate = LocalDate.parse(recentDate, DateTimeFormatter.ofPattern(formatPattern))
//    val period: Period = date1.until(date2)
//    return period.years
//}
//
//fun Long.asStartOfDay(zoneOffset: ZoneOffset = ZoneOffset.UTC): Long {
//    return Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC).toLocalDate().atStartOfDay(zoneOffset).toInstant().toEpochMilli()
//}
