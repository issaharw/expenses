package com.issahar.expenses.handler

import com.issahar.expenses.model.Category

object ServerCache {

    val expenseNameCategoryMap = mutableMapOf<String, Category>()
}