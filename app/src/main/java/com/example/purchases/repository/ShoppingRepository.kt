package com.example.purchases.repository

import com.example.purchases.ui.components.ShoppingItem
import com.example.purchases.ui.components.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun getAllLists(): Flow<List<ShoppingList>>

    suspend fun insertList(list: ShoppingList): Long

    suspend fun deleteList(list: ShoppingList)

    fun getAllItems(): Flow<List<ShoppingItem>>

    suspend fun insertItem(item: ShoppingItem)

    suspend fun  deleteItem(item: ShoppingItem)

    suspend fun updateItem(item: ShoppingItem)
    fun getItemsForList(listId: Int): Flow<List<ShoppingItem>>
}