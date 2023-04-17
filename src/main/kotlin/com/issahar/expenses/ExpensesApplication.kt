package com.issahar.expenses

import com.issahar.expenses.excel.MaxCreditCardFileParser
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.FileInputStream


@SpringBootApplication
class ExpensesApplication

fun main(args: Array<String>) {
//	runApplication<UltraCloudApplication>(*args)
	testing()
}


fun testing() {
	val fis = FileInputStream("/Users/issahar/Downloads/transaction-details-February.xlsx")

	val parser = MaxCreditCardFileParser()
	val expenses = parser.parseFile(fis)
	println(expenses.size)
}