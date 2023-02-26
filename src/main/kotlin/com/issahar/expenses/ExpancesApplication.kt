package com.issahar.expenses

import org.springframework.boot.autoconfigure.SpringBootApplication
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@SpringBootApplication
class expensesApplication

fun main(args: Array<String>) {
//	runApplication<UltraCloudApplication>(*args)
	testing()
}


fun testing() {
	val url = getUploadFileUrl("issahar1.dcm")
	println(url)
}


fun getUploadFileUrl(filePath: String): String {
	val bucketName = "ultrasight-cloud-test"
	val presigner = S3Presigner.builder().region(Region.US_EAST_2)
		.credentialsProvider(ProfileCredentialsProvider.create()).build()
	try {
		val objectRequest = PutObjectRequest
			.builder()
			.key(filePath)
			.bucket(bucketName)
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
		return ""
	}
}