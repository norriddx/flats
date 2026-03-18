package com.example.flats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComparisonCard(
    @SerialName("comparison_card_id") val comparisonCardId: Long = 0,
    @SerialName("comparison_id") val comparisonId: Long = 0,
    @SerialName("card_id") val cardId: Long = 0,
    val position: Int? = null
)