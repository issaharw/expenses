package com.issahar.expenses.di

import com.issahar.expenses.resource.*
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.stereotype.Component

@Component
class JerseyConfig : ResourceConfig() {
    init {
//        register(MultiPartFeature::class.java)
        register(HealthResource::class.java)
        register(CategoryResource::class.java)
//        register(ExpenseResource::class.java)
    }
}
