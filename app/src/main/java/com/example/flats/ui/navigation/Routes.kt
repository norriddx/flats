package com.example.flats.ui.navigation

object Routes {
    const val AUTH        = "auth"
    const val CARDS       = "cards"
    const val CREATE_CARD = "create_card"
    const val EDIT_CARD   = "edit_card/{cardId}"
    const val VIEW_CARD   = "view_card/{cardId}"
    const val COMPARISON  = "comparison"
    const val FAVOURITES  = "favourites"

    fun viewCard(cardId: Long) = "view_card/$cardId"
    fun editCard(cardId: Long) = "edit_card/$cardId"
}