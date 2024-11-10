package com.issahar.expenses.model

data class Category(val id: Int, val name: String, val parent: Int? = null)
