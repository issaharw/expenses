package com.issahar.expenses.resource


import com.issahar.expenses.handler.CategoryService
import com.issahar.expenses.model.Category
import jakarta.inject.Inject
import jakarta.ws.rs.*
import org.springframework.web.bind.annotation.*

@RestController
@Path("/api/categories")
class CategoryResource @Inject constructor(private val categoryService: CategoryService) {

    // Get all categories
    @GET
    @Produces("application/json")
    fun getCategories(): List<Category> {
        return categoryService.getCategories()
    }

    // Add a new category
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    fun addCategory(category: Category): Int = categoryService.addCategory(category)

    // Update an existing category
    @PUT
    @Path("/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun updateCategory(@PathParam("id") id: Int, category: Category) {
        val updatedCategory = category.copy(id = id)
        categoryService.updateCategory(category)
    }
}








//@Path("/categories")
//class CategoryResource @Inject constructor(private val storage: Storage) {
//
//    private val logger = LoggerFactory.getLogger(this.javaClass)
//    @Context
//    private var request: HttpServletRequest? = null
//
//
//
//    @PUT
//    @Path("/upload/{expensesFileType}")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces("application/json")
//    fun uploadFile(@FormDataParam("file") body: FormDataBodyPart, @PathParam("expensesFileType") expensesFileType: String): Boolean {
//        val expensesFileType = ExpensesFileType.fromValue(expensesFileType)
//        logger.info("Received expenses file of type: $expensesFileType")
//        val inputStream: InputStream = body.parent.bodyParts.first().getEntityAs(InputStream::class.java)
//        expenseHandler.handleExpensesFile(inputStream, expensesFileType)
//        return true
//    }
//}
