package com.example.purchases

import com.example.purchases.ui.components.ShoppingList

data class ShoppingListsUiState(
    val isLoading: Boolean = false,
    val lists: List<ShoppingList> = emptyList(),
    val error: String? = null
)