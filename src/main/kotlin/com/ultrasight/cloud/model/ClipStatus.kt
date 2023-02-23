package com.ultrasight.cloud.model

enum class ClipStatus(val value:Int) {
    ERROR_SAVING_TO_STORAGE(-1),
    CREATED(0),
    PROCESSING(1),
    DONE(3),
    ERROR_CALCULATING_RESULTS(4);

    companion object {
        fun fromValue(value: Int):ClipStatus {
            for (type in ClipStatus.values()) {
                if (value == type.value)
                    return type
            }
            return CREATED
        }
    }

    fun isOK(): Boolean = this != ERROR_SAVING_TO_STORAGE
    fun imagesUploadedSuccessfully(): Boolean = this != ERROR_SAVING_TO_STORAGE && this != CREATED
}