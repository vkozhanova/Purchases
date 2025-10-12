package com.example.purchases.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lists")
@Serializable
data class ShoppingList (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "list_id") val listId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean,
)