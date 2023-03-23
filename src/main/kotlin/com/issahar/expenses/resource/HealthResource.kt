package com.issahar.expenses.resource

import jakarta.inject.Inject
import jakarta.ws.rs.*
import org.slf4j.LoggerFactory
import java.io.*

@Path("/health")
class HealthResource {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GET
    @Path("/")
    @Produces("application/json")
    fun healthCheck(): String {
        return "OK"
    }
}
