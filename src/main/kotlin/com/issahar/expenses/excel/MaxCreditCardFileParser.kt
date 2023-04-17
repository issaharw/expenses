package com.issahar.expenses.excel

import com.issahar.expenses.model.Expense
import com.issahar.expenses.model.ExpenseType
import com.issahar.expenses.util.parseDate
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.util.*

class MaxCreditCardFileParser: ExpensesFileParser {
    override fun parseFile(inputStream: InputStream): List<Expense> {
        val myWorkBook = XSSFWorkbook(inputStream)
        val expenses = mutableListOf<Expense>()
        val israelSheet: XSSFSheet = myWorkBook.getSheet("עסקאות במועד החיוב")
        val israelRowIterator = israelSheet.iterator()
        repeat(4) {israelRowIterator.next() }
        while (israelRowIterator.hasNext()) {
            val row = israelRowIterator.next()
            val firstCell = row.getCell(0).stringCellValue
            if (firstCell == "סך הכל")
                break
            expenses.add(getIsraelExpenseFromRow(row))
        }

        val abroadSheet: XSSFSheet = myWorkBook.getSheet("עסקאות חו\"ל ומט\"ח")
        val abroadRowIterator = abroadSheet.iterator()
        repeat(4) {abroadRowIterator.next() }
        while (abroadRowIterator.hasNext()) {
            val row = abroadRowIterator.next()
            val firstCell = row.getCell(0).stringCellValue
            if (firstCell == "סך הכל")
                break
            expenses.add(getAbroadExpenseFromRow(row))
        }

        val immediateSheet: XSSFSheet = myWorkBook.getSheet("עסקאות בחיוב מיידי")
        val immediateRowIterator = immediateSheet.iterator()
        repeat(4) {immediateRowIterator.next() }
        while (immediateRowIterator.hasNext()) {
            val row = immediateRowIterator.next()
            val firstCell = row.getCell(0).stringCellValue
            if (firstCell == "סך הכל")
                break
            expenses.add(getAbroadExpenseFromRow(row))
        }


        return expenses
    }

    private fun getIsraelExpenseFromRow(row: Row): Expense {
        val date = row.getCell(0).stringCellValue.toDate()
        val name  = row.getCell(1).stringCellValue
        val cardName = row.getCell(3).stringCellValue
        val amount = row.getCell(5).numericCellValue
        val originalAmount = row.getCell(7).numericCellValue
        val chargeDate = row.getCell(9).stringCellValue.toDate()
        val comment = row.getCell(10).stringCellValue
        val tags = row.getCell(11).stringCellValue
        return Expense(0, date, chargeDate, amount, name, null, originalAmount, "Comment: $comment. Tags: $tags", ExpenseType.CreditCardIsrael)
    }

    private fun getAbroadExpenseFromRow(row: Row): Expense {
        val date = row.getCell(0).stringCellValue.toDate()
        val name  = row.getCell(1).stringCellValue
        val cardName = row.getCell(3).stringCellValue
        val amount = row.getCell(5).numericCellValue
        val originalAmount = row.getCell(7).numericCellValue
        val originalCurrency = row.getCell(8).stringCellValue
        val chargeDate = row.getCell(9).stringCellValue.toDate()
        return Expense(0, date, chargeDate, amount, name, null, originalAmount, "Card: $cardName. Original Currency: $originalCurrency", ExpenseType.CreditCardAbroad)
    }

    private fun String.toDate(): Date = this.parseDate("dd-MM-yyyy")
}