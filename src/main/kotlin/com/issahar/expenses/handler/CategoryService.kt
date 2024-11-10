package com.issahar.expenses.handler

import com.issahar.expenses.dao.CategoryDao
import com.issahar.expenses.model.Category
import jakarta.inject.Inject
import org.springframework.stereotype.Service

@Service
class CategoryService @Inject constructor(private val categoryDao: CategoryDao) {

    fun getCategories(): List<Category> = categoryDao.getCategories()

    fun addCategory(category: Category): Int = categoryDao.addCategory(category)

    fun updateCategory(category: Category) = categoryDao.updateCategory(category)
}
