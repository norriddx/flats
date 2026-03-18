package com.example.flats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    @SerialName("card_id") val cardId: Long = 0,
    @SerialName("user_id") val userId: String = "",
    val name: String = "",
    val address: String? = null,
    val price: Double? = null,
    val square: Double? = null,
    val description: String? = null,
    @SerialName("is_favourite") val isFavourite: Boolean = false,
    @SerialName("is_draft") val isDraft: Boolean = true
)