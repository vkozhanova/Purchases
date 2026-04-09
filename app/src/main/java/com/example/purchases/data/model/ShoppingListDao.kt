package com.example.purchases.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.purchases.ui.components.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao{

    @Query("SELECT * FROM lists")
    fun getAllLists(): Flow<List<ShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingList): Long

    @Delete
    suspend fun deleteList(list: ShoppingList)
}