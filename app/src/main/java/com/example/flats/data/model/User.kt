package com.example.flats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id") val userId: String = "",
    val username: String = "",
    val phone: String? = null,
    val email: String = "",
    @SerialName("created_at") val createdAt: String? = null
)
