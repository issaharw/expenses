package com.issahar.expenses.storage

import com.issahar.expenses.di.Config
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import jakarta.inject.Inject

@Component
@Profile("local")
class LocalStorage @Inject constructor(val config: Config): Storage {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun saveFile(filePath: String, file: File): Boolean {
        return true
//        return try {
//            val destFile = File(config.localStorageFolder, filePath)
//            destFile.parentFile.mkdirs()
//            destFile.writeBytes(file.readBytes())
//            true
//        }
//        catch (e: IOException) {
//            logger.error("Failed to save file to local disk. Path: $filePath", e)
//            false
//        }
    }

    override fun getFileUrl(filePath: String): String {
        return "/ultracloud-storage/$filePath"
    }

    override fun getUploadFileUrl(filePath: String): String {
        return "${config.dbServerHost}/clips/upload/${filePath.replace("/", "^^")}"
    }

    override fun getFileFromTemp(filePath: String): File {
        return File("${config.dbServerHost}/${filePath}")
    }
}