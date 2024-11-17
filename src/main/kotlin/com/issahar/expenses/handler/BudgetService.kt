package com.issahar.expenses.handler

import com.issahar.expenses.dao.BudgetItemDao
import com.issahar.expenses.dao.CategoryDao
import com.issahar.expenses.excel.ExcelBudgetParser
import com.issahar.expenses.model.BudgetItem
import com.issahar.expenses.model.BudgetItemDO
import com.issahar.expenses.model.Category
import com.issahar.expenses.model.ExpenseNameCategory
import com.issahar.expenses.util.getCurrentBudgetMonth
import jakarta.inject.Inject
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class BudgetService @Inject constructor(private val categoryDao: CategoryDao, private val budgetItemDao: BudgetItemDao) {

    fun getCategories(userId: Int): List<Category> = categoryDao.getCategories(userId)

    fun addCategory(userId: Int, category: Category) = categoryDao.addCategory(userId, category)

    fun getBudgetItems(userId: Int): List<BudgetItem> = budgetItemDao.getBudgetItems(userId)

    fun addBudgetItem(userId: Int, item: BudgetItemDO){
        val category = categoryDao.getCategoryByName(userId, item.category)
        budgetItemDao.addBudgetItem(userId, BudgetItem(item.month, category, item.amount))
    }

    fun updateBudgetItem(userId: Int, budgetMonth: String, categoryName: String, amount: Int) {
        val category = categoryDao.getCategoryByName(userId, categoryName)
        budgetItemDao.updateBudgetItem(userId, budgetMonth, category.name, amount)
    }

    fun getBudgetItemsForCurrentMonth(userId: Int): List<BudgetItem> {
        val currentMonth = getCurrentBudgetMonth()
        return budgetItemDao.getBudgetItemsForMonth(userId, currentMonth)
    }

    fun addExpenseNameCategory(userId: Int, expenseNameCategory: ExpenseNameCategory) {
        categoryDao.addExpenseNameCategory(userId, expenseNameCategory)
        ServerCache.expenseNameCategoryMap[expenseNameCategory.expenseName] = expenseNameCategory.category
    }

    fun parseBudgetExcel(userId: Int, excelIS: InputStream) {
        val parser = ExcelBudgetParser()
        val budgetItems = parser.parseExcelBudget(excelIS)

        // Handle new categories from the budget excel
        val categoriesFromExcel = budgetItems.map { Category(it.category) }.toMutableSet()
        val dbCategories = categoryDao.getCategories(userId)
        categoriesFromExcel.removeAll(dbCategories)
        categoriesFromExcel.forEach { categoryDao.addCategory(userId, it) }

        // Now add or update the budget
        val allBudgetItems = budgetItemDao.getBudgetItems(userId)
        val (toUpdate, toInsert) = budgetItems.partition { item ->
            allBudgetItems.any { it.isTheSame(item) }
        }

        toUpdate.forEach {
            updateBudgetItem(userId, it.month, it.category, it.amount)
        }
        toInsert.forEach {
            addBudgetItem(userId, it)
        }
    }
}
