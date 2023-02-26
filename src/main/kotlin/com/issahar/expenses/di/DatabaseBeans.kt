package com.issahar.expenses.di

import com.ultrasight.cloud.dao.ClipDao
import com.ultrasight.cloud.dao.ExamDao
import com.ultrasight.cloud.dao.PatientDao
import com.ultrasight.cloud.util.mapper
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
import com.ultrasight.cloud.dao.UserDao
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile


@Configuration
class DatabaseBeans @Inject constructor(private val config: Config) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Bean
    @Profile("!local")
    fun dataSource(): DataSource? {
        val (host, user, password) = getDatabaseDetails()
        logger.info("Using DB on AWS. Host: $host")
        return DataSourceBuilder
            .create()
            .url("jdbc:mysql://${host}/cloud")
            .username(user)
            .password(password)
            .build()
    }

    @Bean
    @Profile("local")
    fun dataSourceForLocal(): DataSource? {
        val dbUrl = "jdbc:mysql://${config.dbServerHost}/cloud"
        logger.info("Using local DB. Url: $dbUrl")
        return DataSourceBuilder
            .create()
            .url(dbUrl)
            .username("root")
            .password("Ultrasight1!")
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
    fun patientDao(dbi: Jdbi): PatientDao {
        return dbi.onDemand(PatientDao::class.java)
    }

    @Bean
    fun examDao(dbi: Jdbi): ExamDao {
        return dbi.onDemand(ExamDao::class.java)
    }

    @Bean
    fun clipDao(dbi: Jdbi): ClipDao {
        return dbi.onDemand(ClipDao::class.java)
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