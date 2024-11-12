package com.issahar.expenses.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil


fun Cell.getCellValue(): String {
    val cell = this
    return when (cell.cellType) {
        CellType.STRING -> cell.stringCellValue
        CellType.NUMERIC -> if (DateUtil.isCellDateFormatted(cell)) {
            cell.dateCellValue.toString()
        } else {
            cell.numericCellValue.toString()
        }
        CellType.BOOLEAN -> cell.booleanCellValue.toString()
        CellType.FORMULA -> cell.cellFormula
        CellType.BLANK -> ""
        else -> "Unknown Type"
    }
}