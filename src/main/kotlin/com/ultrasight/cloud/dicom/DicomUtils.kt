package com.ultrasight.cloud.dicom

import com.ultrasight.cloud.model.DicomData
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.io.DicomInputStream
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import org.dcm4che3.util.ByteUtils
import java.math.BigInteger
import java.util.*


private const val ULTRASIGHT_UID_PREFIX = "1.2.826.0.1.3680043.10.292"

private val logger = LoggerFactory.getLogger("DicomUtils")

fun generateUUID(): String {
    return toUID(UUID.randomUUID())
}

private fun toUID(uuid: UUID, root: String = ULTRASIGHT_UID_PREFIX): String {
    val b17 = ByteArray(17)
    ByteUtils.longToBytesBE(uuid.mostSignificantBits, b17, 2)
    ByteUtils.longToBytesBE(uuid.leastSignificantBits, b17, 9)
    val uuidStr = BigInteger(b17).toString()
    val rootlen = root.length
    val uuidlen = uuidStr.length
    val cs = CharArray(rootlen + uuidlen + 1)
    root.toCharArray(cs, 0, 0, rootlen)
    cs[rootlen] = '.'
    uuidStr.toCharArray(cs, rootlen + 1, 0, uuidlen)
    return String(cs)
}

fun getDicomImages(file: File, numberOfImages: Int): List<BufferedImage> {
    return getDicomImages(DicomInputStream(file), numberOfImages)
}

fun getDicomImages(inputStream: InputStream, numberOfImages: Int): List<BufferedImage> {
    return getDicomImages(DicomInputStream(inputStream), numberOfImages)
}

private fun getDicomImages(dicomInputStream: DicomInputStream, numberOfImages: Int): List<BufferedImage> {
    val imageReader = ImageIO.getImageReadersByFormatName("DICOM").next()
    dicomInputStream.use { dis ->
        imageReader.input = dis
        return (0 until numberOfImages).map { index ->
            imageReader.read(index)
        }
    }
}

fun getDicomSingleImage(file: File, imageIndex: Int): BufferedImage {
    return getDicomImage(DicomInputStream(file), imageIndex)
}

private fun getDicomImage(dicomInputStream: DicomInputStream, imageIndex: Int): BufferedImage {
    val imageReader = ImageIO.getImageReadersByFormatName("DICOM").next()
    dicomInputStream.use { dis ->
        imageReader.input = dis
        return imageReader.read(imageIndex)
    }
}

fun getDicomData(dcmFile: File): DicomData? {
    try {
        val dis = DicomInputStream(dcmFile)
        val dcmObj: Attributes = dis.readDataset()
        val patientID = dcmObj.getString(Tag.PatientID)
        val studyID = dcmObj.getString(Tag.StudyInstanceUID)?.ifEmpty { null } ?: generateUUID()
        val instanceID = dcmObj.getString(Tag.SOPInstanceUID)?.ifEmpty { null } ?: generateUUID()
        val studyDate = dcmObj.getString(Tag.StudyDate)?.ifEmpty { null } ?: "19700101"
        var studyTime = dcmObj.getString(Tag.StudyTime)?.ifEmpty { null } ?: "000001.000000"
        val contentDate = dcmObj.getString(Tag.ContentDate)?.ifEmpty { null } ?: dcmObj.getString(Tag.AcquisitionDate)?.ifEmpty { null } ?: "19700101"
        val contentTime = dcmObj.getString(Tag.ContentTime)?.ifEmpty { null } ?: dcmObj.getString(Tag.AcquisitionTime)?.ifEmpty { null } ?: "000001.000000"
        var clipTime = contentDate + contentTime
        if (!clipTime.contains('.')) clipTime = "$clipTime.000000"
        if (!studyTime.contains('.')) studyTime = "$studyTime.000000"
        val patientName = dcmObj.getString(Tag.PatientName)
        val patientSex = dcmObj.getString(Tag.PatientSex)
        val patientBirthDate = dcmObj.getString(Tag.PatientBirthDate)
        val numberOfFrames = dcmObj.getInt(Tag.NumberOfFrames, 1)
        val performing = dcmObj.getString(Tag.PerformingPhysicianName)?.ifEmpty { null } ?: dcmObj.getString(Tag.OperatorsName)?: ""
        val attending = dcmObj.getString(Tag.PhysiciansOfRecord)?: ""
        val accessionNumber = dcmObj.getString(Tag.AccessionNumber)?: ""
        val manufacturerModel = dcmObj.getString(Tag.ManufacturerModelName)?: ""
        val transducerData = dcmObj.getString(Tag.TransducerData)?: ""
        val processingFunction = dcmObj.getString(Tag.ProcessingFunction)?: ""
        val cineRate = dcmObj.getInt(Tag.CineRate, 0)
        return DicomData(patientID, studyID, instanceID, studyDate, studyTime , patientName, patientSex, patientBirthDate,
            clipTime, numberOfFrames, performing, attending, accessionNumber, manufacturerModel, transducerData, processingFunction, cineRate)
    }
    catch (e: Exception) {
        logger.error("Cannot create DicomData from file: ${dcmFile.absolutePath}", e)
        return null
    }
}