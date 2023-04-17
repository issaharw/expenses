package com.issahar.expenses.excel

import com.issahar.expenses.model.Expense
import com.issahar.expenses.model.ExpenseType
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

class PoalimCreditCardFileParser: ExpensesFileParser {
    override fun parseFile(inputStream: InputStream): List<Expense> {
        val myWorkBook = XSSFWorkbook(inputStream)
        val mySheet: XSSFSheet = myWorkBook.getSheetAt(0)
        val expenses = mutableListOf<Expense>()
        val israelRowIterator = mySheet.iterator()
        var lastRowNum = findIsraelChargesFirstLine(israelRowIterator)
        while (israelRowIterator.hasNext()) {
            val row = israelRowIterator.next()
            println("Row Number: ${row.rowNum}")
            if (row.rowNum > lastRowNum + 1)
                break
            else
                lastRowNum = row.rowNum
            expenses.add(getIsraelExpenseFromRow(row))
        }
        val abroadRowIterator = mySheet.iterator()
        lastRowNum = findAbroadChargesFirstLine(abroadRowIterator)
        while (abroadRowIterator.hasNext()) {
            val row = abroadRowIterator.next()
            println("Row Number: ${row.rowNum}")
            if (row.rowNum > lastRowNum + 1)
                break
            else
                lastRowNum = row.rowNum
            expenses.add(getAbroadExpenseFromRow(row))
        }
        return expenses
    }

    private fun findIsraelChargesFirstLine(rowIterator: Iterator<Row>): Int = findChargesFirstLine(rowIterator, "פירוט עבור הכרטיסים בארץ")
    private fun findAbroadChargesFirstLine(rowIterator: Iterator<Row>): Int = findChargesFirstLine(rowIterator, "פירוט עבור הכרטיסים בחו''ל")

    private fun findChargesFirstLine(rowIterator: Iterator<Row>, textToFind: String): Int {
        var rowNum = -1
        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val firstCell = row.getCell(0)
            val text = if (firstCell.cellType == CellType.STRING)
                firstCell.stringCellValue
            else
                firstCell.numericCellValue.toString()
            if (text == textToFind) {
                rowNum = row.rowNum
                break
            }
        }
        if (rowNum >= 0) {
            repeat(2) {
                rowIterator.next()
            }
            rowNum += 2
        }

        return rowNum
    }

    private fun getIsraelExpenseFromRow(row: Row): Expense {
        val cardName = row.getCell(0).stringCellValue
        val chargeDate = row.getCell(1).dateCellValue
        val date = row.getCell(2).dateCellValue
        val name  = row.getCell(3).stringCellValue
        val originalAmount = row.getCell(4).numericCellValue
        val amount = row.getCell(5).numericCellValue
        val asmachtaCell = row.getCell(6)
        val asmachta = if (asmachtaCell.cellType == CellType.NUMERIC)
            asmachtaCell.numericCellValue.toInt()
        else
            asmachtaCell.stringCellValue.toInt()
        return Expense(0, date, chargeDate, amount, name, asmachta, originalAmount, "Card Name: $cardName", ExpenseType.CreditCardIsrael)
    }

    private fun getAbroadExpenseFromRow(row: Row): Expense {
        val cardName = row.getCell(0).stringCellValue
        val chargeDate = row.getCell(1).dateCellValue
        val date = row.getCell(2).dateCellValue
        val name  = row.getCell(3).stringCellValue
        val amount = row.getCell(4).numericCellValue
        val originalAmount = row.getCell(5).numericCellValue
        val originalCurrency = row.getCell(6).stringCellValue
        val asmachtaCell = row.getCell(7)
        val asmachta = if (asmachtaCell.cellType == CellType.NUMERIC)
            asmachtaCell.numericCellValue.toInt()
        else
            asmachtaCell.stringCellValue.toInt()
        return Expense(0, date, chargeDate, amount, name, asmachta, originalAmount, "Card: $cardName. Original Currency: $originalCurrency", ExpenseType.CreditCardAbroad)
    }
}