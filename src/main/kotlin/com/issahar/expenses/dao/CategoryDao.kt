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


@RegisterRowMappers(RegisterRowMapper(CategoryMapper::class))
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
    @GetGeneratedKeys
    fun addCategory(@Bind userId: Int, @BindBean category: Category): Int
}


class CategoryMapper : RowMapper<Category> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Category {
        return Category(
            resultSet.getString("name"),
            resultSet.getString("parent_category")
        )
    }
}
