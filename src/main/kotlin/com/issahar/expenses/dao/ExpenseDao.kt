package com.issahar.expenses.dao

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.SqlObject
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.config.RegisterRowMappers
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.transaction.Transactional
import java.sql.ResultSet
import com.issahar.expenses.model.*
import com.issahar.expenses.util.*


@RegisterRowMappers(RegisterRowMapper(ExpenseMapper::class))
interface ExpenseDao : SqlObject, Transactional<ExpenseDao> {

    @SqlQuery(
        """select
            id,
            expense_date,
            charge_date,
            amount,
            name,
            asmacta,
            original_amount,
            details,
            expense_type,
            creation_time
          from Expenses"""
    )
    fun getExpenses(): List<Expense>

    @SqlUpdate("""INSERT INTO Expenses
          (
            expense_date,
            charge_date,
            amount,
            name,
            asmacta,
            original_amount,
            details,
            expense_type,
            creation_time
          )
          VALUES (
            :date,
            :chargeDate,
            :amount,
            :name,
            :asmachta,
            :originalAnmount,
            :details,
            :expenseTypeValue,
            FROM_UNIXTIME(:creationTime * 0.001)
          )"""
    )
    @GetGeneratedKeys
    fun addExpense(@BindBean expense: Expense): Int
}

class ExpenseMapper : RowMapper<Expense> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Expense {
        return Expense(
            resultSet.getInt("id"),
            resultSet.getDate("expense_date"),
            resultSet.getDate("charge_date"),
            resultSet.getFloat("amount").toDouble(),
            resultSet.getString("name"),
            resultSet.getInt("asmachta"),
            resultSet.getFloat("original_amount").toDouble(),
            resultSet.getString("details"),
            ExpenseType.fromValue(resultSet.getInt("expense_type")),
            resultSet.getTimestamp("creation_time").time
        )
    }
}

