package com.ultrasight.cloud.model

enum class ClipType(val value:Int) {
    CLIP(1),
    IMAGE(2);

    companion object {
        fun fromValue(value: Int): ClipType {
            for (type in ClipType.values()) {
                if (value == type.value)
                    return type
            }
            return CLIP
        }
    }
}