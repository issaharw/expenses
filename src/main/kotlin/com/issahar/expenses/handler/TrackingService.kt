package com.issahar.expenses.handler

import com.issahar.expenses.dao.CategoryDao
import com.issahar.expenses.dao.ExpenseDao
import com.issahar.expenses.excel.ParserFactory
import com.issahar.expenses.model.*
import com.issahar.expenses.util.getCurrentBudgetMonth
import jakarta.inject.Inject
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class TrackingService @Inject constructor(private val expenseDao: ExpenseDao,
                                          private val parserFactory: ParserFactory,
                                          private val categoryDao: CategoryDao) {

    fun getExpenses(userId: Int): List<Expense> = expenseDao.getExpenses(userId)

    fun getExpensesForMonth(userId: Int, chargeMonth: String): List<Expense> = expenseDao.getExpensesForMonth(userId, chargeMonth)

    fun getExpensesForCurrentMonth(userId: Int): List<Expense> = expenseDao.getExpensesForMonth(userId, getCurrentBudgetMonth())

    fun addExpense(userId: Int, expense: Expense): Int {
        val expenseMap = getExpenseNameMap(userId)
        val expenseCategory = expenseMap[expense.name]
        val expenseWithCategory = expense.copy(category = expenseCategory)
        val expenseId = expenseDao.addExpense(userId, expenseWithCategory)
        return expenseId
    }

    private fun getExpenseNameMap(userId: Int): Map<String, Category> {
        if (ServerCache.expenseNameCategoryMap.isEmpty()) {
            val expenseNameCategories = categoryDao.getExpenseNameCategories(userId)
            val expenseMap = expenseNameCategories.associate { Pair(it.expenseName, it.category) }
            ServerCache.expenseNameCategoryMap.putAll(expenseMap)
        }

        return ServerCache.expenseNameCategoryMap
    }

    fun parseExpensesExcel(userId: Int, excelIS: InputStream, fileType: ExpensesFileType): Int {
        val parser = parserFactory.getParser(fileType)
        val expenses = parser.parseFile(excelIS).filter { it.amount != 0.0 }
        expenses.forEach {
            addExpense(userId, it)
        }
        return expenses.size
    }
}
