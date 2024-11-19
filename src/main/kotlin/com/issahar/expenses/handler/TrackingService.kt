package com.issahar.expenses.handler

import com.issahar.expenses.dao.CategoryDao
import com.issahar.expenses.dao.ExpenseDao
import com.issahar.expenses.excel.ParserFactory
import com.issahar.expenses.model.*
import com.issahar.expenses.util.getCurrentBudgetMonth
import jakarta.inject.Inject
import org.springframework.stereotype.Service
import java.io.InputStream
import java.time.LocalDate

@Service
class TrackingService @Inject constructor(private val expenseDao: ExpenseDao,
                                          private val parserFactory: ParserFactory,
                                          private val categoryDao: CategoryDao,
                                          private val budgetService: BudgetService) {

    fun getExpenses(userId: Int): List<Expense> = expenseDao.getExpenses(userId)

    fun getExpensesForMonth(userId: Int, chargeMonth: String): List<Expense> = expenseDao.getExpensesForMonth(userId, chargeMonth)

    fun getExpensesForCurrentMonth(userId: Int): List<Expense> = expenseDao.getExpensesForMonth(userId, getCurrentBudgetMonth())

    fun addExpense(userId: Int, expense: Expense): Expense {
        val expenseMap = getExpenseNameMap(userId)
        val expenseCategory = expenseMap[expense.name]
        val expenseWithCategory = expense.copy(category = expenseCategory)
        expenseDao.addExpense(userId, expenseWithCategory)
        return expenseWithCategory
    }

    private fun getExpenseNameMap(userId: Int): Map<String, Category> {
        if (ServerCache.expenseNameCategoryMap.isEmpty()) {
            val expenseNameCategories = categoryDao.getExpenseNameCategories(userId)
            val expenseMap = expenseNameCategories.associate { Pair(it.expenseName, it.category) }
            ServerCache.expenseNameCategoryMap.putAll(expenseMap)
        }

        return ServerCache.expenseNameCategoryMap
    }

    fun parseExpensesExcel(userId: Int, excelIS: InputStream, fileType: ExpensesFileType): List<Expense> {
        val parser = parserFactory.getParser(fileType)
        val expenses = parser.parseFile(excelIS).filter { it.amount != 0.0 }
        val expensesToInsert = getExpensesToInsertToDB(userId, expenses)
        val expensesWithCategories = expensesToInsert.map {
            addExpense(userId, it)
        }
        return expensesWithCategories.sortedByDescending { it.date }
    }

    private fun getExpensesToInsertToDB(userId: Int, expenses: List<Expense>): List<Expense> {
        val budgetMonths = expenses.map { it.budgetMonth }.distinct()
        val dbExpenses = if (budgetMonths.size == 1)
            expenseDao.getExpensesForMonth(userId, budgetMonths.first())
        else
            expenseDao.getExpensesForMonths(userId, budgetMonths)

        return expenses.filter { !dbExpenses.contains(it) }
    }

    fun getCurrentMonthTracking(userId: Int): List<CurrentMonthTrackingDO> {
        val budgetItems = budgetService.getBudgetItemsForCurrentMonth(userId)
        val expenses = getExpensesForCurrentMonth(userId)
        val trackingItems = budgetItems.map { budgetItem ->
            val categoryName = budgetItem.category.name
            val budgetAmount = budgetItem.amount
            val expensesSum = expenses.filter { it.category?.name == categoryName }.sumOf { it.amount }
            val lastUpdate = if (expensesSum != 0.0)
                expenses.filter { it.category?.name == categoryName }.maxByOrNull { it.date }!!.date
            else
                LocalDate.MIN
            CurrentMonthTrackingDO(categoryName, budgetAmount, expensesSum, lastUpdate)
        }

        return trackingItems
    }
}
