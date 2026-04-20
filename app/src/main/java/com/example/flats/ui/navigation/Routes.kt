package com.example.flats.ui.navigation

object Routes {
    const val AUTH        = "auth"
    const val CARDS       = "cards"
    const val CREATE_CARD = "create_card"
    const val VIEW_CARD   = "view_card/{cardId}"
    const val COMPARISON  = "comparison"

    fun viewCard(cardId: Long) = "view_card/$cardId"
}