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
import org.jdbi.v3.sqlobject.customizer.BindList


@RegisterRowMappers(RegisterRowMapper(ExpenseMapper::class))
interface ExpenseDao : SqlObject, Transactional<ExpenseDao> {

    @SqlQuery(
        """SELECT
            id,
            expense_date,
            e.name,
            amount,
            budget_month,
            asmachta,
            original_amount,
            details,
            expense_type,
            category_name,
            parent_category,
            creation_time
          FROM Expenses as e
          LEFT JOIN Categories as c on (e.category_name = c.name)
          WHERE e.user_id = :userId"""
    )
    fun getExpenses(@Bind userId: Int): List<Expense>

    @SqlQuery(
        """SELECT
            id,
            expense_date,
            e.name,
            amount,
            budget_month,
            asmachta,
            original_amount,
            details,
            expense_type,
            category_name,
            parent_category,
            creation_time
          FROM Expenses as e
          LEFT JOIN Categories as c on (e.category_name = c.name)
          WHERE e.user_id = :userId AND budget_month = :budgetMonth"""
    )
    fun getExpensesForMonth(@Bind userId: Int, @Bind budgetMonth: String): List<Expense>

    @SqlQuery(
        """SELECT
            id,
            expense_date,
            e.name,
            amount,
            budget_month,
            asmachta,
            original_amount,
            details,
            expense_type,
            category_name,
            parent_category,
            creation_time
          FROM Expenses as e
          LEFT JOIN Categories as c on (e.category_name = c.name)
          WHERE e.user_id = :userId AND budget_month in (<budgetMonths>)"""
    )
    fun getExpensesForMonths(@Bind userId: Int, @BindList("budgetMonths") budgetMonths: List<String>): List<Expense>

    @SqlUpdate("""INSERT INTO Expenses
          (
            user_id,
            expense_date,
            name,
            amount,
            budget_month,
            asmachta,
            original_amount,
            details,
            expense_type,
            category_name,
            creation_time
          )
          VALUES (
            :userId,
            :date,
            :name,
            :amount,
            :budgetMonth,
            :asmachta,
            :originalAmount,
            :details,
            :expenseTypeValue,
            :category?.name,
            FROM_UNIXTIME(:creationTime * 0.001)
          )"""
    )
    @GetGeneratedKeys
    fun addExpense(@Bind userId: Int, @BindBean expense: Expense): Int
}

class ExpenseMapper : RowMapper<Expense> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Expense {
        val categoryName = resultSet.getString("category_name")
        val category = if (categoryName != null) {
            Category(
                resultSet.getString("category_name"),
                resultSet.getString("parent_category")
            )
        }
        else
            null
        return Expense(
            resultSet.getInt("id"),
            resultSet.getDate("expense_date").toLocalDate(),
            resultSet.getString("name"),
            resultSet.getDouble("amount"),
            resultSet.getString("budget_month"),
            resultSet.getInt("asmachta"),
            resultSet.getDouble("original_amount"),
            resultSet.getString("details"),
            ExpenseType.fromValue(resultSet.getInt("expense_type")),
            category,
            resultSet.getTimestamp("creation_time").time
        )
    }
}

