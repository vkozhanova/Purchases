package com.example.purchases.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.purchases.features.ui.components.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem)

    @Query("SELECT * FROM items WHERE list_id = :listId")
    fun getItemsForList(listId: Int): Flow<List<ShoppingItem>>

    @Delete
    suspend fun deleteItem(item: ShoppingItem)

    @Update
    suspend fun updateItem(item: ShoppingItem)
}