package com.ultrasight.cloud.di

import com.ultrasight.cloud.resource.*
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.stereotype.Component

@Component
class JerseyConfig : ResourceConfig() {
    init {
        register(MultiPartFeature::class.java)
        register(HealthResource::class.java)
        register(ExamResource::class.java)
        register(ClipResource::class.java)
    }
}
