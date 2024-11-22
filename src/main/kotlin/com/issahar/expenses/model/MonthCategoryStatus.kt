package com.issahar.expenses.model

import java.time.LocalDate

data class MonthCategoryStatus(val categoryName: String, val budgetMonth: String, val budgetAmount: Int, val expensesSum: Double, val lastUpdated: LocalDate)
