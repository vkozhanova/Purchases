package com.example.purchases.repository

import com.example.purchases.data.model.ShoppingItemDao
import com.example.purchases.data.model.ShoppingListDao
import com.example.purchases.ui.components.ShoppingItem
import com.example.purchases.ui.components.ShoppingList
import kotlinx.coroutines.flow.Flow

class ShoppingRepositoryImpl(
    private val listDao: ShoppingListDao,
    private val itemDao: ShoppingItemDao
): ShoppingRepository {

    override fun getAllLists(): Flow<List<ShoppingList>> = listDao.getAllLists()

    override suspend fun insertList(list: ShoppingList): Long = listDao.insertList(list)

    override suspend fun deleteList(list: ShoppingList) = listDao.deleteList(list)

    override fun getAllItems(): Flow<List<ShoppingItem>> = itemDao.getAllItems()

    override suspend fun insertItem(item: ShoppingItem) = itemDao.insertItem(item)

    override suspend fun  deleteItem(item: ShoppingItem) = itemDao.deleteItem(item)

    override suspend fun updateItem(item: ShoppingItem) = itemDao.updateItem(item)

    override fun getItemsForList(listId: Int): Flow<List<ShoppingItem>> = itemDao.getItemsForList(listId)
}