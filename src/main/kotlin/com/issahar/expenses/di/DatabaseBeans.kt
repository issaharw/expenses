package com.issahar.expenses.di

import com.issahar.expenses.util.mapper
import org.jdbi.v3.core.Jdbi
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import jakarta.inject.Singleton
import jakarta.inject.Inject
import javax.sql.DataSource
import com.fasterxml.jackson.module.kotlin.readValue
import com.issahar.expenses.dao.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile


@Configuration
class DatabaseBeans @Inject constructor(private val config: Config) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun dataSourceForLocal(): DataSource? {
        val dbUrl = "jdbc:mysql://${config.dbServerHost}/expenses"
        logger.info("Using local DB. Url: $dbUrl")
        return DataSourceBuilder
            .create()
            .url(dbUrl)
            .username(config.dbUser)
            .password(config.dbPassword)
            .build()
    }

    @Bean
    @Singleton
    fun createJdbi(dataSource: DataSource): Jdbi {
        val dbi = Jdbi.create(dataSource)
        dbi.installPlugins()
        return dbi
    }

    @Bean
    fun categoryDao(dbi: Jdbi): CategoryDao {
        return dbi.onDemand(CategoryDao::class.java)
    }

    @Bean
    fun userDao(dbi: Jdbi): UserDao {
        return dbi.onDemand(UserDao::class.java)
    }

    private fun getDatabaseDetails(): Triple<String, String, String> {
        val client = SecretsManagerClient.builder().region(Region.US_EAST_2).build()
        val getSecretValueRequest = GetSecretValueRequest.builder().secretId(config.databaseSecretKey).build()

        val getSecretValueResponse = client.getSecretValue(getSecretValueRequest)
        val secret = getSecretValueResponse.secretString()
        val secretValues = mapper.readValue<Map<String,String>>(secret)
        return Triple(secretValues["host"]!! + ":" + secretValues["port"]!!, secretValues["username"]!!, secretValues["password"]!!)
    }
}