package com.issahar.expenses

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.FileInputStream


@SpringBootApplication
class ExpensesApplication

fun main(args: Array<String>) {
//	runApplication<UltraCloudApplication>(*args)
	testing()
}


fun testing() {
	val fis = FileInputStream("/Users/issahar/Downloads/excelNewTransactions.xlsx")

	val myWorkBook = XSSFWorkbook(fis)
	val mySheet: XSSFSheet = myWorkBook.getSheetAt(0)
	val rowIterator = mySheet.iterator()
	while (rowIterator.hasNext()) {
		val row = rowIterator.next()
		val cellIterator = row.cellIterator()
		while (cellIterator.hasNext()) {
			val cell = cellIterator.next()
			when {
				cell.cellType == CellType.STRING -> print(cell.stringCellValue + "\t")
				cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell) -> print(cell.dateCellValue.toString() + "\t")
				cell.cellType == CellType.NUMERIC -> print(cell.numericCellValue.toString() + "\t")
				cell.cellType == CellType.BOOLEAN -> print(cell.booleanCellValue.toString() + "\t")
				else -> print("\t")
			}
		}
		println("")
	}}