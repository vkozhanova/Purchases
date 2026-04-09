package com.example.purchases.ui.components

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "items")
@Serializable
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "list_id") val listId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean = false
    )
