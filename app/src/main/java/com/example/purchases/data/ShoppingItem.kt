package com.example.purchases.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "items")
@Serializable
data class ShoppingItem(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    )
