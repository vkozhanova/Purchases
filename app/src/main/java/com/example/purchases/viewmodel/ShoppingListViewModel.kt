package com.example.purchases.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.purchases.repository.ShoppingRepository
import com.example.purchases.ui.components.ShoppingItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val repository: ShoppingRepository,
    private val listId: Int
): ViewModel() {
    val items: StateFlow<List<ShoppingItem>> = repository.getItemsForList(listId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addItem(name: String) {
        viewModelScope.launch {
            repository.insertItem(ShoppingItem(listId = listId, name = name))
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun copyItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.insertItem(item.copy(id = 0, name = item.name + " (копия)"))
        }
    }

    fun updateItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun toggleChecked(item: ShoppingItem) {
        updateItem(item.copy(isChecked = !item.isChecked))
    }
}