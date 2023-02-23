package com.ultrasight.cloud.resource

import com.fasterxml.jackson.module.kotlin.readValue
import com.ultrasight.cloud.handler.ClipHandler
import com.ultrasight.cloud.model.NetworkResults
import com.ultrasight.cloud.model.UploadFileDetails
import com.ultrasight.cloud.storage.Storage
import com.ultrasight.cloud.util.mapper
import com.ultrasight.cloud.util.now
import org.glassfish.jersey.media.multipart.FormDataBodyPart
import org.glassfish.jersey.media.multipart.FormDataParam
import org.slf4j.LoggerFactory
import java.io.*
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType


@Path("/clips")
class ClipResource @Inject constructor(private val clipHandler: ClipHandler, private val storage: Storage) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    @Context
    private var request: HttpServletRequest? = null

    private fun getUserId() = request?.getAttribute("userId")!! as Int

    @GET
    @Path("/upload-url")
    @Produces("application/json")
    fun getUploadFileUrl(@QueryParam("fileName") fileName: String): UploadFileDetails {
        val filePath = "${getUserId()}/${now()}-$fileName"
        val uploadUrl = storage.getUploadFileUrl(filePath)
        return UploadFileDetails(filePath, uploadUrl)
    }

    @PUT
    @Path("/upload/{filePath}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    fun uploadFile(@FormDataParam("file") body: FormDataBodyPart, @PathParam("filePath") filePath: String): Boolean {
        val localFilePath = filePath.replace("^^", "/")
        logger.info("Received Dicom file: $localFilePath")
        val inputStream: InputStream = body.parent.bodyParts.first().getEntityAs(InputStream::class.java)
        val clipFile = clipHandler.saveClipFileToTemp(localFilePath, inputStream)
        return clipFile != null
    }

    @POST
    @Path("/handle")
    @Produces("application/json")
    fun handleUploadedFile(@QueryParam("filePath") filePath: String): Int {
        logger.info("Handling already uploaded Dicom file: $filePath")
        val clip = clipHandler.handleClip(filePath)
        return clip?.id ?: -1
    }

    @POST
    @Path("/delete")
    @Consumes("application/json")
    fun deleteClips(clipIds: String) {
        try {
            logger.info("Going to delete the following clips: $clipIds.")
            val clipIdsToDelete = mapper.readValue<List<Int>>(clipIds)
            clipHandler.deleteClips(clipIdsToDelete)
        }
        catch (e: Exception) {
            logger.error("Error deleting the following clips from DB: $clipIds", e)
        }
    }

    @POST
    @Path("/{clipId}/quality-results")
    @Consumes("application/json")
    fun saveClipResults(@PathParam("clipId") clipId: Int, qualityResults: String) {
        try {
            logger.info("Got Network results for clip ID: $clipId.")
            mapper.readValue<NetworkResults>(qualityResults)
            clipHandler.saveQualityResults(clipId, qualityResults)
        }
        catch (e: Exception) {
            logger.error("Network results are in incorrect format: $qualityResults", e)
            throw e
        }
    }

    @POST
    @Path("/{clipId}/quality-results-error")
    @Consumes("application/json")
    fun saveClipResultsErrors(@PathParam("clipId") clipId: Int, qualityResults: String) {
        val errors = mapper.readValue<Map<String, String>>(qualityResults)
        logger.warn("Got Network results ERROR for clip ID: $clipId. Errors: ${errors["error"]}")
        clipHandler.saveQualityResultsError(clipId)
    }
}
