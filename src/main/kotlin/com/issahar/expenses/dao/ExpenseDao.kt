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


@RegisterRowMappers(RegisterRowMapper(ExpenseMapper::class))
interface ExpenseDao : SqlObject, Transactional<ExpenseDao> {

    @SqlQuery(
        """select
            id,
            expense_date,
            name,
            amount,
            charge_month,
            asmacta,
            original_amount,
            details,
            expense_type,
            creation_time
          from Expenses
          WHERE user_id = :userId"""
    )
    fun getExpenses(@Bind userId: Int): List<Expense>

    @SqlQuery(
        """select
            id,
            expense_date,
            name,
            amount,
            charge_month,
            asmacta,
            original_amount,
            details,
            expense_type,
            creation_time
          from Expenses
          WHERE user_id = :userId AND charge_month = :chargeMonth"""
    )
    fun getExpensesForMonth(@Bind userId: Int, @Bind chargeMonth: String): List<Expense>

    @SqlUpdate("""INSERT INTO Expenses
          (
            user_id,
            expense_date,
            name,
            amount,
            charge_month,
            asmacta,
            original_amount,
            details,
            expense_type,
            creation_time
          )
          VALUES (
            :userId,
            :date,
            :name,
            :amount,
            :chargeMonth,
            :asmachta,
            :originalAmount,
            :details,
            :expenseTypeValue,
            FROM_UNIXTIME(:creationTime * 0.001)
          )"""
    )
    @GetGeneratedKeys
    fun addExpense(@Bind userId: Int, @BindBean expense: Expense): Int
}

class ExpenseMapper : RowMapper<Expense> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Expense {
        return Expense(
            resultSet.getInt("id"),
            resultSet.getDate("expense_date"),
            resultSet.getString("name"),
            resultSet.getFloat("amount").toDouble(),
            resultSet.getString("charge_month"),
            resultSet.getInt("asmachta"),
            resultSet.getFloat("original_amount").toDouble(),
            resultSet.getString("details"),
            ExpenseType.fromValue(resultSet.getInt("expense_type")),
            resultSet.getTimestamp("creation_time").time
        )
    }
}

