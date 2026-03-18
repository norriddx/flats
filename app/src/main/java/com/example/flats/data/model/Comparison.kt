package com.example.flats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comparison(
    @SerialName("comparison_id") val comparisonId: Long = 0,
    @SerialName("user_id") val userId: String = "",
    @SerialName("created_at") val createdAt: String? = null
)