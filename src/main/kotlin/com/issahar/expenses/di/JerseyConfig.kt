package com.issahar.expenses.di

import com.issahar.expenses.resource.*
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.stereotype.Component

@Component
class JerseyConfig : ResourceConfig() {
    init {
        register(HealthResource::class.java)
        register(BudgetResource::class.java)
//        register(ExpenseResource::class.java)
    }
}
