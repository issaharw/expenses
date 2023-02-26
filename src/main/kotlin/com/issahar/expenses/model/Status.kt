package com.issahar.expenses.model

enum class Status(val value:Int) {
    ERROR(-1),
    CREATED(0),
    PROCESSING(1),
    READY(3);

    companion object {
        fun fromValue(value: Int):Status {
            for (type in Status.values()) {
                if (value == type.value)
                    return type
            }
            return CREATED
        }
    }
}