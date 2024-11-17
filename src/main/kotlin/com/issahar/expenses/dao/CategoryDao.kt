package com.issahar.expenses.dao

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.SqlObject
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.config.RegisterRowMappers
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.transaction.Transactional
import java.sql.ResultSet
import com.issahar.expenses.model.*


@RegisterRowMappers(RegisterRowMapper(CategoryMapper::class), RegisterRowMapper(ExpenseNameCategoryMapper::class))
interface CategoryDao : SqlObject, Transactional<CategoryDao> {

    @SqlQuery(
        """SELECT name, parent_category FROM Categories
           WHERE user_id = :userId
        """
    )
    fun getCategories(@Bind userId: Int): List<Category>

    @SqlQuery(
        """SELECT name, parent_category FROM Categories
           WHERE name = :name
           AND user_id = :userId
        """
    )
    fun getCategoryByName(@Bind userId: Int, @Bind name: String): Category

    @SqlQuery(
        """SELECT enc.expense_name, enc.category_name, c.parent_category 
           FROM ExpenseNameCategory as enc JOIN Categories as c on (enc.category_name = c.name)
           WHERE enc.user_id = :userId
        """
    )
    fun getExpenseNameCategories(@Bind userId: Int): List<ExpenseNameCategory>

    @SqlUpdate(
        """INSERT INTO Categories
          (
            user_id,
            name,
            parent_category
          )
          VALUES (
            :userId,
            :name,
            :parent
          )"""
    )
    fun addCategory(@Bind userId: Int, @BindBean category: Category)

    @SqlUpdate(
        """INSERT INTO ExpenseNameCategory
          (
            user_id,
            expense_name,
            category_name
          )
          VALUES (
            :userId,
            :expenseName
            :category.name
          )"""
    )
    fun addExpenseNameCategory(@Bind userId: Int, @BindBean expenseNameCategory: ExpenseNameCategory)
}


class CategoryMapper : RowMapper<Category> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Category {
        return Category(
            resultSet.getString("name"),
            resultSet.getString("parent_category")
        )
    }
}

class ExpenseNameCategoryMapper : RowMapper<ExpenseNameCategory> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): ExpenseNameCategory {
        val category = Category(
            resultSet.getString("category_name"),
            resultSet.getString("parent_category")
        )

        return ExpenseNameCategory(
            resultSet.getString("expense_name"),
            category
        )
    }
}
