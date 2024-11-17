package com.issahar.expenses.excel

import com.issahar.expenses.model.ExpensesFileType
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class ParserFactory {
    fun getParser(type: ExpensesFileType): ExpensesFileParser {
        return when (type) {
            ExpensesFileType.BankPoalimTransactions -> PoalimTransactionsFileParser()
            ExpensesFileType.CreditCardFromPoalim -> PoalimCreditCardFileParser()
            ExpensesFileType.CreditCardFromMax -> MaxCreditCardFileParser()
            ExpensesFileType.CreditCardFromCal -> CalCreditCardFileParser()
            else -> throw IllegalArgumentException("Parser of type $type is not supported")
        }
    }
}