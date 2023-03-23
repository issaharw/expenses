package com.issahar.expenses.excel

import com.issahar.expenses.model.Expense
import com.issahar.expenses.model.ExpenseType
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

class PoalimTransactionsFileParser: ExpensesFileParser {
    override fun parseFile(inputStream: InputStream): List<Expense> {
        val myWorkBook = XSSFWorkbook(inputStream)
        val mySheet: XSSFSheet = myWorkBook.getSheetAt(0)
        val expenses = mutableListOf<Expense>()
        val rowIterator = mySheet.iterator()
        repeat(3) {
            println(rowIterator.next().rowNum)
        }
        rowIterator.forEachRemaining { row ->
            println("Row Number: ${row.rowNum}")
            expenses.add(getExpenseFromRow(row))
        }
        return expenses
    }


    private fun getExpenseFromRow(row: Row): Expense {
        val date = row.getCell(0).dateCellValue
        val name  = row.getCell(1).stringCellValue
        val details  = row.getCell(2)?.stringCellValue
        val asmachta = row.getCell(3).numericCellValue.toInt()
        val amount = getAmount(row)
        return Expense(0, date, date, amount, name, asmachta, null, details, ExpenseType.Bank)
    }

    private fun getAmount(row: Row): Double {
        val chovaCell = row.getCell(4)
        if (chovaCell?.cellType == CellType.NUMERIC)
            return chovaCell.numericCellValue

        return if (chovaCell == null || chovaCell.cellType == CellType.STRING && chovaCell.stringCellValue.isNullOrEmpty())
            row.getCell(5).numericCellValue * -1
        else
            0.0
    }
}