package com.issahar.expenses.resource

import com.issahar.expenses.handler.TrackingService
import com.issahar.expenses.model.Expense
import com.issahar.expenses.model.ExpensesFileType
import org.slf4j.LoggerFactory
import java.io.*
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context

@Path("/api/tracking")
class TrackingResource @Inject constructor(private val trackingService: TrackingService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    @Context
    private var request: HttpServletRequest? = null
    private val userId: Int
        get() = request?.getAttribute("userId").toString().toInt()

    @GET
    @Path("/expenses")
    @Produces("application/json")
    fun getExpenses(): List<Expense> {
        return trackingService.getExpenses(userId)
    }

    @GET
    @Path("/expenses/current-month")
    @Produces("application/json")
    fun getExpensesForCurrentMonth(): List<Expense> {
        return trackingService.getExpensesForCurrentMonth(userId)
    }

    @POST
    @Path("/expenses")
    @Consumes("application/json")
    @Produces("application/json")
    fun addExpense(expense: Expense): Expense = trackingService.addExpense(userId, expense)

    @PUT
    @Path("/upload/{expensesFileType}")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    fun uploadFile(@PathParam("expensesFileType") fileType: String, requestInputStream: InputStream): List<Expense> {
        val expensesFileType = ExpensesFileType.fromValue(fileType)
        logger.info("Received expenses file of type: $expensesFileType")
        return trackingService.parseExpensesExcel(userId, requestInputStream, expensesFileType)
    }
}
