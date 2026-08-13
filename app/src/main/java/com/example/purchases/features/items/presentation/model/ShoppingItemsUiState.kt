package com.example.purchases.features.items.presentation.model

import com.example.purchases.data.database.entity.ShoppingItem

class ShoppingItemsUiState(
    val isLoading: Boolean = false,
    val items: List<ShoppingItem> = emptyList(),
    val error: String? = null
)