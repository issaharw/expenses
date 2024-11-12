package com.issahar.expenses.resource


import com.issahar.expenses.handler.BudgetService
import com.issahar.expenses.model.BudgetItem
import com.issahar.expenses.model.BudgetItemDO
import com.issahar.expenses.model.Category
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import org.springframework.web.bind.annotation.*
import java.io.InputStream

@RestController
@Path("/api/budget")
class BudgetResource @Inject constructor(private val budgetService: BudgetService) {

    @Context
    private var request: HttpServletRequest? = null

    private val userId: Int
        get() = request?.getAttribute("userId").toString().toInt()

    @GET
    @Path("/categories")
    @Produces("application/json")
    fun getCategories(): List<Category> {
        return budgetService.getCategories(userId)
    }

    @POST
    @Path("/categories")
    @Consumes("application/json")
    @Produces("application/json")
    fun addCategory(category: Category): Int = budgetService.addCategory(userId, category)

    @POST
    @Path("/categories/multiple")
    @Consumes("application/json")
    @Produces("application/json")
    fun addCategory(categories: List<Category>): Int {
        categories.forEach {
            budgetService.addCategory(userId, it)
        }
        return categories.size
    }

    @PUT
    @Path("/categories/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun updateCategory(@PathParam("id") id: Int, category: Category) {
        val updatedCategory = category.copy(id = id)
        budgetService.updateCategory(updatedCategory)
    }

    @GET
    @Path("/budgetItems")
    @Produces("application/json")
    fun getBudgetItems(): List<BudgetItem> {
        return budgetService.getBudgetItems(userId)
    }

    @GET
    @Path("/budgetItems/currentMonth")
    @Produces("application/json")
    fun getBudgetItemsForCurrentMonth(): List<BudgetItem> {
        return budgetService.getBudgetItemsForCurrentMonth(userId)
    }

    @POST
    @Path("/budgetItems")
    @Consumes("application/json")
    @Produces("application/json")
    fun addBudgetItem(budgetItem: BudgetItemDO) = budgetService.addBudgetItem(userId, budgetItem)

    @PUT
    @Path("/budgetItems")
    @Consumes("application/json")
    @Produces("application/json")
    fun updateCategory(data: BudgetItemDO) {
        budgetService.updateBudgetItem(userId, data.month, data.category, data.amount)
    }

    @PUT
    @Path("/upload")
//    @Consumes(MediaType.MAPPLICATION_OCTET_STREAM) not setting it to octet, because postman uses excel content type
    @Produces("application/json")
    fun uploadFile(requestInputStream: InputStream): Boolean {
        budgetService.parseBudgetExcel(userId, requestInputStream)
        return true
    }

}
