package com.example.purchases.features.lists.presentation.model

import com.example.purchases.features.ui.components.ShoppingList

data class ShoppingListsUiState(
    val isLoading: Boolean = false,
    val lists: List<ShoppingList> = emptyList(),
    val error: String? = null
)