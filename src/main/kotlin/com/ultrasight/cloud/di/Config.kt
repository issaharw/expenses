package com.ultrasight.cloud.di

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class Config {

    @Value("\${ai.server.host}")
    val aiServerHost: String? = null

    @Value("\${db.server.host}")
    val dbServerHost: String? = null

    @Value("\${be.server.host}")
    val beServerHost: String? = null

    @Value("\${s3.bucket.name}")
    val s3BucketName: String? = null

    @Value("\${ULTRACLOUD_ROOT_FOLDER}")
    val localStorageRootFolder: String? = null

    @Value("\${DATABASE_SECRET_KEY}")
    val databaseSecretKey: String? = null

    val tempFolder: File
        get() = File(localStorageRootFolder, "ultracloud-temp")

    val localStorageFolder: File
        get() = File(localStorageRootFolder, "ultracloud-storage")
}