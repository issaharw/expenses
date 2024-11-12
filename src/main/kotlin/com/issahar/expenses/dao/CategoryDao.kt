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


@RegisterRowMappers(RegisterRowMapper(CategoryMapper::class))
interface CategoryDao : SqlObject, Transactional<CategoryDao> {

    @SqlQuery(
        """SELECT id, category, parent_category FROM Categories
           WHERE user_id = :userId
        """
    )
    fun getCategories(@Bind userId: Int): List<Category>

    @SqlQuery(
        """SELECT id, category, parent_category FROM Categories
           WHERE category = :name
           AND user_id = :userId
        """
    )
    fun getCategoryByName(@Bind userId: Int, @Bind name: String): Category

    @SqlUpdate("""INSERT INTO Categories
          (
            user_id,
            category,
            parent_category
          )
          VALUES (
            :userId,
            :name,
            :parent
          )"""
    )
    @GetGeneratedKeys
    fun addCategory(@Bind userId: Int, @BindBean category: Category): Int

    @SqlUpdate("""UPDATE Categories SET
          category = :name,
          parent_category = :parent
          WHERE id = :id"""
    )
    fun updateCategory(@BindBean category: Category)

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

//class ExpenseNameCategoryMapper : RowMapper<ExpenseNameCategory> {
//    override fun map(resultSet: ResultSet, statementContext: StatementContext): ExpenseNameCategory {
//        val category = Category(
//            resultSet.getInt("id"),
//            resultSet.getString("category"),
//            resultSet.getInt("parent_category")
//        )
//        return ExpenseNameCategory(
//            resultSet.getString("expense_name"),
//            category
//        )
//    }
//}

