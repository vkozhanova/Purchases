package com.example.purchases.features.items.presentation.model

import com.example.purchases.features.ui.components.ShoppingItem

class ShoppingItemsUiState(
    val isLoading: Boolean = false,
    val items: List<ShoppingItem> = emptyList(),
    val error: String? = null
)