package com.issahar.expenses.model

import com.issahar.expenses.util.now
import java.util.Date

data class Expense(val id: Int,
                   val date: Date,
                   val name: String,
                   val amount: Double,
                   val chargeMonth: String,
                   val asmachta: Int? = null,
                   val originalAmount: Double? = null,
                   val details: String? = null,
                   val expenseType: ExpenseType = ExpenseType.Bank,
                   val creationTime: Long = now()) {
    val expenseTypeValue = expenseType.value
}
