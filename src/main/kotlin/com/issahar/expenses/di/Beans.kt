package com.issahar.expenses.di

import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Configuration
class Beans {

    @Bean
    fun getS3Client(): S3Client {
        val region = Region.US_EAST_2
        return S3Client.builder()
            .region(region)
            .build()
    }
}

object Async {
    private const val poolSize = 30
    val pool: ExecutorService = Executors.newFixedThreadPool(poolSize, ThreadFactoryBuilder().setNameFormat("Async-tasks-%d").build())
}