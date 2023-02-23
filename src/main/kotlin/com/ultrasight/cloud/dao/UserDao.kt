package com.ultrasight.cloud.dao

import com.ultrasight.cloud.model.User
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.SqlObject
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.config.RegisterRowMappers
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.transaction.Transactional
import java.sql.ResultSet

@RegisterRowMappers(RegisterRowMapper(UserMapper::class))
interface UserDao : SqlObject, Transactional<UserDao> {

    @SqlQuery(
        """select
            id,
            email,
            first_name,
            last_name,
            is_active
          from Users
          where email = :email"""
    )
    fun getUserByEmail(@Bind("email") email: String): User?
}

class UserMapper : RowMapper<User> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): User {
        return User(
            resultSet.getInt("id"),
            resultSet.getString("email"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getBoolean("is_active")
        )
    }
}