package com.ultrasight.cloud.resource

import com.ultrasight.cloud.di.Config
import com.ultrasight.cloud.util.httpGet
import org.slf4j.LoggerFactory
import java.io.*
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response

@Path("/health")
class HealthResource @Inject constructor(private val config: Config) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GET
    @Path("/")
    @Produces("application/json")
    fun healthCheck(): String {
        return "OK"
    }

    @GET
    @Path("/with-ai-server")
    @Produces("application/json")
    fun healthCheckWithAI(): Response {
        val url = "${config.aiServerHost}/api/health"
        return try {
            val response = httpGet(url)
            if (response.statusCode == 200)
                Response.ok("OK").build()
            else {
                logger.error("AI Server returned an error. Host: $url. Response code: ${response.statusCode}")
                Response.status(500, "AI Server returned an error").build()
            }
        }
        catch (e: Exception) {
            logger.error("AI Server call failed. Host: $url", e)
            Response.status(500, "AI Server returned an error").build()
        }
    }
}
