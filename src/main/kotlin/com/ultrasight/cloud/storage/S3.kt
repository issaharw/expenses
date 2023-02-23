package com.ultrasight.cloud.storage

import com.ultrasight.cloud.di.Config
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.File
import java.time.Duration
import jakarta.inject.Inject
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest


@Component
@Profile("!local")
class S3 @Inject constructor(val s3Client: S3Client,
                             val config: Config): Storage {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun saveFile(filePath: String, file: File): Boolean {
        return try {
            val putOb = PutObjectRequest.builder()
                .bucket(config.s3BucketName)
                .key(filePath)
                .build()

            val requestBody = RequestBody.fromFile(file)
            val response = s3Client.putObject(putOb, requestBody)
            true
        } catch (e: S3Exception) {
            logger.error("Failed to save file to S3. Path: $filePath", e)
            false
        }
    }

    override fun getFileUrl(filePath: String): String {
        val presigner = S3Presigner.create()
        try {
            val objectRequest = GetObjectRequest
                .builder()
                .key(filePath)
                .bucket(config.s3BucketName)
                .build()
            val getObjectPresignRequest = GetObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofDays(1))
                .getObjectRequest(objectRequest)
                .build()
            val presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest)
            return presignedGetObjectRequest.url().toString()
        } catch (e: Exception) {
            logger.error("Failed to presign file. Path: $filePath", e)
            return ""
        }
    }

    override fun getUploadFileUrl(filePath: String): String {
        val presigner = S3Presigner.create()
        try {
            val objectRequest = PutObjectRequest
                .builder()
                .key(filePath)
                .bucket(config.s3BucketName)
                .contentType("multipart/form-data")
                .build()
            val putObjectPresignRequest = PutObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofDays(1))
                .putObjectRequest(objectRequest)
                .build()
            val presignedGetObjectRequest = presigner.presignPutObject(putObjectPresignRequest)
            return presignedGetObjectRequest.url().toString()
        } catch (e: Exception) {
            logger.error("Failed to presign upload file. Path: $filePath", e)
            return ""
        }
    }

    override fun getFileFromTemp(filePath: String): File? {
        try {
            val objectRequest = GetObjectRequest
                .builder()
                .key(filePath)
                .bucket(config.s3BucketName)
                .build()
            val objectBytes: ResponseBytes<GetObjectResponse> = s3Client.getObjectAsBytes(objectRequest)
            val data: ByteArray = objectBytes.asByteArray()

            // Write the data to a local file.
            val tempFile = File("${config.tempFolder}/${filePath}")
            tempFile.writeBytes(data)
            return tempFile
        } catch (e: S3Exception) {
            logger.error("Failed to get file from S3 temp", e)
            return null
        }
    }

/*
        fun listObjects() {
            try {
                val listObjects = ListObjectsRequest
                    .builder()
                    .bucket("onsight-med-public")
                    .build()

                val res = s3Client.listObjects(listObjects)
                val objects = res.contents()
                for (myValue in objects) {
                    println("\n The name of the key is " + myValue.key())
                    println("\n The owner is " + myValue.owner())
                }
            } catch (e: S3Exception) {
                println(e.message)
                e.printStackTrace()
            }
        }

    */
}