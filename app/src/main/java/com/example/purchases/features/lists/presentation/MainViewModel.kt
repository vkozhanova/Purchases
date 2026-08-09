package com.example.purchases.features.lists.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.purchases.features.lists.presentation.model.ShoppingListsUiState
import com.example.purchases.data.database.repository.ShoppingRepository
import com.example.purchases.features.ui.components.ShoppingList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ShoppingRepository): ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListsUiState(isLoading = true))
    val uiState: StateFlow<ShoppingListsUiState> = _uiState.asStateFlow()

    init{
        loadShoppingLists()
    }

    private fun loadShoppingLists() {
        viewModelScope.launch {
            repository.getAllLists()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { lists ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            lists = lists,
                            error = null
                        )
                    }
                }
        }
    }
    fun deleteList(list: ShoppingList) {
        viewModelScope.launch {
            repository.deleteList(list)
        }
    }

    fun copyList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            val copiedList = shoppingList.copy(id = 0, name = shoppingList.name + " (копия)")
            val copyListId = repository.insertList(copiedList).toInt()

            val itemsCurrentList = repository.getItemsForList(shoppingList.id).first()

            itemsCurrentList.forEach { item ->
                repository.insertItem(item.copy(id = 0, listId = copyListId))
            }
        }
    }

    fun createNewList() {
        viewModelScope.launch {
            repository.insertList(ShoppingList(name = "Новый список"))
        }
    }

    fun renameList(shoppingList: ShoppingList, newName: String) {
        viewModelScope.launch {
            repository.insertList(shoppingList.copy(name = newName))
        }
    }
}