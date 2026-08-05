package com.example.purchases

import com.example.purchases.ui.components.ShoppingItem

class ShoppingItemsUiState(
    val isLoading: Boolean = false,
    val items: List<ShoppingItem> = emptyList(),
    val error: String? = null
)