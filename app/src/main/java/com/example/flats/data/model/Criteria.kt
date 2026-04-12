package com.example.flats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Criteria(
    @SerialName("criteria_id") val criteriaId: Long = 0,
    @SerialName("user_id") val userId: String = "",
    val name: String = "",
    val weight: Double = 1.0,
    @SerialName("is_active") val isActive: Boolean = true,
    val type: String = "score"
)