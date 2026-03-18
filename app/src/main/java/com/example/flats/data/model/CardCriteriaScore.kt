package com.example.flats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardCriteriaScore(
    @SerialName("score_id") val scoreId: Long = 0,
    @SerialName("card_id") val cardId: Long = 0,
    @SerialName("criteria_id") val criteriaId: Long = 0,
    val value: Double = 0.0
)