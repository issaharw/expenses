package com.ultrasight.cloud.model

data class User(val id: Int,
                val email: String,
                val firstName: String?,
                val lastName: String?,
                val isActive: Boolean)
