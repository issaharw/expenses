package com.issahar.expenses.model

import java.time.LocalDate

data class CurrentMonthTrackingDO(val categoryName: String, val budgetAmount: Int, val expensesSum: Double, val lastUpdated: LocalDate)
