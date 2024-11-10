package com.issahar.expenses.di

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class Config {

    @Value("\${db.server.host}")
    val dbServerHost: String? = null

    @Value("\${db.user}")
    val dbUser: String? = null

    @Value("\${db.password}")
    val dbPassword: String? = null

    @Value("\${s3.bucket.name}")
    val s3BucketName: String? = null
}