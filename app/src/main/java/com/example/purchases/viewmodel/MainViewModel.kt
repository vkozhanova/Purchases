package com.example.purchases.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.purchases.data.ShoppingList
import com.example.purchases.repository.ShoppingRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ShoppingRepository): ViewModel() {

    private val _shoppingLists = MutableLiveData<List<ShoppingList>>()
    val shoppingList: LiveData<List<ShoppingList>>
        get() = _shoppingLists

    init {
        loadShoppingLists()
    }

    private fun loadShoppingLists() {
        viewModelScope.launch {
            val lists = repository.getAllLists()
            _shoppingLists.value = lists as List<ShoppingList>?

        }
    }

    fun deleteList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            repository.deleteList(shoppingList)
            loadShoppingLists()
        }
    }

    fun copyList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            val copiedList = shoppingList.copy(id = 0,
                name = shoppingList.name + " (копия)")

            val copiedListId = repository.insertList(copiedList).toInt()

           repository.getAllItems().collect { allItems ->

               val itemsOfThisList = allItems.filter { it.listId == shoppingList.id }

               itemsOfThisList.forEach { item ->
                   val copiedItem = item.copy(id = 0,
                       listId = copiedListId
                   )
                   repository.insertItem(copiedItem)
               }
               return@collect
           }

        }
        loadShoppingLists()
    }
}