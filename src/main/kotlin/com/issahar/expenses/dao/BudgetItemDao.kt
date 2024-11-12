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


@RegisterRowMappers(RegisterRowMapper(BudgetItemMapper::class))
interface BudgetItemDao : SqlObject, Transactional<BudgetItemDao> {

    @SqlQuery(
        """SELECT b.budget_month, b.category_id, b.amount, c.category, c.parent_category 
           FROM BudgetItems as b join Categories as c on (b.category_id = c.id)
           WHERE b.user_id = :userId"""
    )
    fun getBudgetItems(userId: Int): List<BudgetItem>

    @SqlUpdate("""INSERT INTO BudgetItems
          (
            user_id,
            budget_month,
            category_id,
            amount
          )
          VALUES (
            :userId,
            :budgetMonth,
            :category.id,
            :amount
          )"""
    )
    fun addBudgetItem(@Bind userId: Int, @BindBean budgetItem: BudgetItem)

    @SqlUpdate("""UPDATE BudgetItems SET
          amount = :amount
          WHERE budget_month = :month AND category_id = :categoryId"""
    )
    fun updateBudgetItem(@Bind userId: Int, @Bind month: String, @Bind categoryId: Int, @Bind amount: Int)

}


class BudgetItemMapper : RowMapper<BudgetItem> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): BudgetItem {
        val category =  Category(
            resultSet.getInt("category_id"),
            resultSet.getString("category"),
            resultSet.getInt("parent_category")
        )
        return BudgetItem(
            resultSet.getString("budget_month"),
            category,
            resultSet.getInt("amount")
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

