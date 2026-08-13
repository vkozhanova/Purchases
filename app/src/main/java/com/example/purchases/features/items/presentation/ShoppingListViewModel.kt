package com.example.purchases.features.items.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.purchases.features.items.presentation.model.ShoppingItemsUiState
import com.example.purchases.data.repository.ShoppingRepository
import com.example.purchases.data.database.entity.ShoppingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val repository: ShoppingRepository,
    private val listId: Int
): ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingItemsUiState(isLoading = true))
    val uiState: StateFlow<ShoppingItemsUiState> = _uiState.asStateFlow()

    init{
        viewModelScope.launch {
            repository.getItemsForList(listId)
                .catch { e ->
                    _uiState.value = ShoppingItemsUiState(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { items ->
                    _uiState.value = ShoppingItemsUiState(
                        isLoading = false,
                        items = items
                    )
                }
        }
    }

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