package com.issahar.expenses.excel

import com.issahar.expenses.model.Expense
import com.issahar.expenses.model.ExpenseType
import com.issahar.expenses.util.*
import kotlinx.coroutines.delay
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.util.*

class CalCreditCardFileParser: ExpensesFileParser {
    override fun parseFile(inputStream: InputStream): List<Expense> {
        val myWorkBook = XSSFWorkbook(inputStream)
        val expenses = mutableListOf<Expense>()
        val israelSheet: XSSFSheet = myWorkBook.getSheetAt(0)
        val israelRowIterator = israelSheet.iterator()
        repeat(2) {israelRowIterator.next() }
        val budgetMonthString = israelRowIterator.next().getCell(0).stringCellValue
        val budgetMonth = parseBudgetMonth(budgetMonthString)
        repeat(2) {israelRowIterator.next() }
        while (israelRowIterator.hasNext()) {
            val row = israelRowIterator.next()
            val firstCell = row.getCell(0).getCellValue()
            if (firstCell.isEmpty() || firstCell.startsWith("את המידע המלא על כל עסקה אפשר למצוא באתר ובאפליקציית כאל"))
                break
            expenses.add(getIsraelExpenseFromRow(row, budgetMonth))
        }

        return expenses
    }

    private fun parseBudgetMonth(budgetMonthString: String): String {
        try {
            val dateString = budgetMonthString.split("-")[1].split(":")[0]
            return dateString.toDate().budgetMonthFromChargeDate()
        }
        catch (e: Exception) {
            println("There is a problem parsing the budget date!. Date String: $budgetMonthString")
            return getCurrentBudgetMonth()
        }
    }

    private fun getIsraelExpenseFromRow(row: Row, budgetMonth: String): Expense {
        val date = row.getCell(0).dateCellValue
        val name  = row.getCell(1).stringCellValue
        val originalAmount = row.getCell(2).numericCellValue
        val amountStr = row.getCell(3).getCellValue()
        val amount = if (amountStr.isEmpty()) 0.0 else amountStr.toDouble()
        val comment = row.getCell(5).stringCellValue
        return Expense(0, date, name, amount, budgetMonth, null, originalAmount, "Category: $comment.", ExpenseType.CreditCardIsrael)
    }



    private fun String.toDate(): Date = this.parseDate("dd/MM/yyyy")
}