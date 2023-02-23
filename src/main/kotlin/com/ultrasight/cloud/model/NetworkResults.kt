package com.ultrasight.cloud.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.ultrasight.cloud.util.mapper

class NetworkResults (val qualities: Map<String,Double>?,
                      @JsonProperty("clip_quality") val clipQuality: Double?,
                      @JsonProperty("frames_quality") val framesQuality: Map<Int,Double>?,
                      @JsonProperty("identified_view") val identifiedView: String?,
                      @JsonProperty("best_frame_quality") val bestFrameQuality: Double?) {
    override fun toString(): String {
        return mapper.writeValueAsString(this)
    }
}