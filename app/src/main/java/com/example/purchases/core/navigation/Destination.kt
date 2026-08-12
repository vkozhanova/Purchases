package com.example.purchases.core.navigation

sealed class Destination {
    object AllLists : Destination()
    data class Edit(val listId: Int, val listName: String) : Destination()
}