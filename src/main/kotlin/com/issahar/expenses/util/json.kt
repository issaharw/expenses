package com.issahar.expenses.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


fun improvedJsonMapper(): ObjectMapper {

    /**
     * IMPORTANT NOTE: improvedJsonMapper should be configured EXACTLY like withKotlin as the default
     * Otherwise some parts of the system will encode in way x, and others try to decode it in way y - a big trouble!
     */

    return jacksonObjectMapper().applySettings()
}


fun ObjectMapper.applySettings(): ObjectMapper {
    return this
        .registerModule(KotlinModule()) // Allows using 1. `@JsonProperty` without `:field`, 2. A single constructor (no need to set default values in all fields)
//            .registerModule(JavaTimeModule()) // Allow to serialize Java 8 Date/Time API (e.g. LocalDate)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // Allow deserialize json with unknown (= extra) properties similar to @JsonIgnoreProperties(ignoreUnknown = true)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true) // Throws an error if primitive types have null
}


val mapper = improvedJsonMapper()