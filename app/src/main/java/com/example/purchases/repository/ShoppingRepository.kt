package com.example.purchases.repository

import com.example.purchases.ui.components.ShoppingItem
import com.example.purchases.data.model.ShoppingItemDao
import com.example.purchases.ui.components.ShoppingList
import com.example.purchases.data.model.ShoppingListDao
import kotlinx.coroutines.flow.Flow

class ShoppingRepository(
    private val itemDao: ShoppingItemDao,
    private val listDao: ShoppingListDao
) {
    fun getAllLists(): Flow<List<ShoppingList>> = listDao.getAllLists()

    suspend fun insertList(list: ShoppingList) = listDao.insertList(list)

    suspend fun deleteList(list: ShoppingList) = listDao.deleteList(list)

    fun getAllItems(): Flow<List<ShoppingItem>> = itemDao.getAllItems()

    suspend fun insertItem(item: ShoppingItem) = itemDao.insertItem(item)

    suspend fun  deleteItem(item: ShoppingItem) = itemDao.deleteItem(item)

}