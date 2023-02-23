package com.ultrasight.cloud.util

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun httpPost(url: String, jsonBody: String): ResponseBodyAndStatus {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return ResponseBodyAndStatus(response.statusCode(), response.body())
}

fun httpGet(url: String): ResponseBodyAndStatus {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .uri(URI.create(url))
        .GET()
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return ResponseBodyAndStatus(response.statusCode(), response.body())
}

data class ResponseBodyAndStatus(val statusCode: Int, val body: String)