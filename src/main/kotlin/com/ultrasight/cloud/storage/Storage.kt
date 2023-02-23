package com.ultrasight.cloud.storage

import java.io.File


interface Storage {
    fun saveFile(filePath: String, file: File): Boolean
    fun getFileUrl(filePath: String): String
    fun getUploadFileUrl(filePath: String): String
    fun getFileFromTemp(filePath: String): File?
}