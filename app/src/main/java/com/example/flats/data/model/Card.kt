package com.example.flats.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val cardId: Long = 0,
    val userId: String = "",
    val name: String = "",
    val address: String? = null,
    val price: Double? = null,
    val square: Double? = null,
    val description: String? = null,
    val isFavourite: Boolean = false,
    val isDraft: Boolean = false,
    val utilitiesIncluded: Boolean = false,
    val imageUrl: String? = null,
    val pricePeriod: String? = null
)