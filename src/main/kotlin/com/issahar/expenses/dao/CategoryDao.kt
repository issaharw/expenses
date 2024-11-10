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


@RegisterRowMappers(RegisterRowMapper(CategoryMapper::class), RegisterRowMapper(ExpenseNameCategoryMapper::class))
interface CategoryDao : SqlObject, Transactional<CategoryDao> {

    @SqlQuery(
        """SELECT expense_name, c.id, c.category, c.parent_category
           FROM ExpenseNameCategory JOIN Categories c ON (c.id = category_id)"""
    )
    fun getExpenseNameCategories(): List<ExpenseNameCategory>

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

class CategoryMapper : RowMapper<Category> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Category {
        return Category(
            resultSet.getInt("id"),
            resultSet.getString("category"),
            resultSet.getInt("parent_category")
        )
    }
}

class ExpenseNameCategoryMapper : RowMapper<ExpenseNameCategory> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): ExpenseNameCategory {
        val category = Category(
            resultSet.getInt("id"),
            resultSet.getString("category"),
            resultSet.getInt("parent_category")
        )
        return ExpenseNameCategory(
            resultSet.getString("expense_name"),
            category
        )
    }
}

