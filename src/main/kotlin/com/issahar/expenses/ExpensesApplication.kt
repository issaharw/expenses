package com.issahar.expenses

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class ExpensesApplication

fun main(vararg args: String) {
	runApplication<ExpensesApplication>(*args)
}
