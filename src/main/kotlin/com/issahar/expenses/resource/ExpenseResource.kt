package com.issahar.expenses.resource

import com.issahar.expenses.handler.ExpenseHandler
import com.issahar.expenses.model.ExpensesFileType
import com.issahar.expenses.storage.Storage
import org.glassfish.jersey.media.multipart.FormDataBodyPart
import org.glassfish.jersey.media.multipart.FormDataParam
import org.slf4j.LoggerFactory
import java.io.*
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

@Path("/expenses")
class ExpenseResource @Inject constructor(private val expenseHandler: ExpenseHandler, private val storage: Storage) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    @Context
    private var request: HttpServletRequest? = null

    @PUT
    @Path("/upload/{expensesFileType}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    fun uploadFile(@FormDataParam("file") body: FormDataBodyPart, @PathParam("expensesFileType") expensesFileType: String): Boolean {
        val expensesFileType = ExpensesFileType.fromValue(expensesFileType)
        logger.info("Received expenses file of type: $expensesFileType")
        val inputStream: InputStream = body.parent.bodyParts.first().getEntityAs(InputStream::class.java)
        expenseHandler.handleExpensesFile(inputStream, expensesFileType)
        return true
    }
}
