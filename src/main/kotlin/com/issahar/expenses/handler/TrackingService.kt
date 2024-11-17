package com.issahar.expenses.handler

import com.issahar.expenses.dao.ExpenseDao
import com.issahar.expenses.excel.ExcelBudgetParser
import com.issahar.expenses.model.*
import com.issahar.expenses.util.getCurrentBudgetMonth
import jakarta.inject.Inject
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class TrackingService @Inject constructor(private val expenseDao: ExpenseDao) {

    fun getExpenses(userId: Int): List<Expense> = expenseDao.getExpenses(userId)

    fun getExpensesForMonth(userId: Int, chargeMonth: String): List<Expense> = expenseDao.getExpensesForMonth(userId, chargeMonth)

    fun getExpensesForCurrentMonth(userId: Int): List<Expense> = expenseDao.getExpensesForMonth(userId, getCurrentBudgetMonth())

    fun addExpense(userId: Int, expense: Expense): Int {
        val expenseId = expenseDao.addExpense(userId, expense)
        // Handle categories!!!!!!
        return expenseId
    }

    fun parseExpensesExcel(userId: Int, excelIS: InputStream, fileType: ExpensesFileType) {
    }
}
