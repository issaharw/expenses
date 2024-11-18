package com.issahar.expenses.model

import com.issahar.expenses.util.now
import java.time.LocalDate

data class Expense(val id: Int,
                   val date: LocalDate,
                   val name: String,
                   val amount: Double,
                   val budgetMonth: String,
                   val asmachta: Int? = null,
                   val originalAmount: Double? = null,
                   val details: String? = null,
                   val expenseType: ExpenseType = ExpenseType.Bank,
                   val category: Category? = null,
                   val creationTime: Long = now()) {
    val expenseTypeValue = expenseType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Expense

        if (date != other.date) return false
        if (name != other.name) return false
        if (amount != other.amount) return false
        if (budgetMonth != other.budgetMonth) return false
//        if (asmachta != other.asmachta) return false
        if (originalAmount != other.originalAmount) return false
        if (details != other.details) return false
        if (expenseType != other.expenseType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + budgetMonth.hashCode()
//        result = 31 * result + (asmachta ?: 0)
        result = 31 * result + (originalAmount?.hashCode() ?: 0)
        result = 31 * result + (details?.hashCode() ?: 0)
        result = 31 * result + expenseType.hashCode()
        return result
    }


}
