package com.issahar.expenses.excel

import com.issahar.expenses.model.BudgetItemDO
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.io.InputStream

class ExcelBudgetParser {

    fun parseExcelBudget(inputStream: InputStream): List<BudgetItemDO> {
        val items = mutableListOf<BudgetItemDO>()
        try {
            inputStream.use { fis ->
                val workbook: Workbook = XSSFWorkbook(fis)
                val sheet: Sheet = workbook.getSheetAt(0)

                val lastRowNum = sheet.lastRowNum
                val maxColumns = sheet.getRow(0).lastCellNum.toInt()

                val categoryMap = buildCategoryMap(lastRowNum, sheet)

                // Iterate over columns
                for (colIndex in 1 until maxColumns) {
                    val month = sheet.getRow(0).getCell(colIndex).getCellValue()
                    println("Month: $month")
                    for (rowIndex in 1..lastRowNum) {
                        val row = sheet.getRow(rowIndex)
                        if (row != null) {
                            val cell = row.getCell(colIndex)
                            if (cell != null) {
                                val category = categoryMap[rowIndex]!!
                                val amount = cell.getCellValue().toFloat().toInt()
                                val item = BudgetItemDO(month, category, amount)
                                items.add(item)
                                println("Item ($rowIndex): $item")
                            }
                        }
                    }
                    println()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return items
   }

    private fun buildCategoryMap(lastRowNum: Int, sheet: Sheet): Map<Int, String> {
        val categoryMap = mutableMapOf<Int, String>()
        for (rowIndex in 1..lastRowNum) {
            categoryMap[rowIndex] = sheet.getRow(rowIndex).getCell(0).getCellValue()
        }
        return categoryMap
    }
}