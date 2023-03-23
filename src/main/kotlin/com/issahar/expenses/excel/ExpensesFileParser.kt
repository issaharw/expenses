package com.issahar.expenses.excel

import com.issahar.expenses.model.Expense
import java.io.InputStream

interface ExpensesFileParser {
    fun parseFile(inputStream: InputStream ): List<Expense>
}