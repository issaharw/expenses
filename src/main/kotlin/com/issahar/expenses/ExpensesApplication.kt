package com.issahar.expenses

import com.issahar.expenses.excel.ExcelBudgetParser
import com.issahar.expenses.excel.MaxCreditCardFileParser
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream


@SpringBootApplication
class ExpensesApplication

fun main(vararg args: String) {
	runApplication<ExpensesApplication>(*args)
//	testing()
}


fun testing() {
	val fis = FileInputStream("/Users/issahar/Downloads/budget.xlsx")

	val parser = ExcelBudgetParser()
	val items = parser.parseExcelBudget(fis)
	println(items.size)
}