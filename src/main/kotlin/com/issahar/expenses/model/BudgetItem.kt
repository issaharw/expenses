package com.issahar.expenses.model

data class BudgetItem(val budgetMonth: String, val category: Category, val amount: Int) {

    fun isTheSame(budgetItemDO: BudgetItemDO) = budgetMonth == budgetItemDO.month && category.name == budgetItemDO.category
}
