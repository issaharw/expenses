package com.issahar.expenses.handler

import com.issahar.expenses.model.Category
import org.springframework.stereotype.Component

@Component
class CategoriesHandler {
    companion object {
        val expenseNameToCategory = mutableMapOf<String, Category>()
    }


}