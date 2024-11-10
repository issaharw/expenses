package com.issahar.expenses

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
	val fis = FileInputStream("/Users/issahar/Downloads/transaction-details-February.xlsx")

	val parser = MaxCreditCardFileParser()
	val expenses = parser.parseFile(fis)
	println(expenses.size)
}