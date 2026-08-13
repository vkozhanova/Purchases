package com.example.purchases.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import com.example.purchases.data.database.entity.ShoppingItem
import com.example.purchases.data.database.dao.ShoppingItemDao
import com.example.purchases.features.ui.components.ShoppingList
import com.example.purchases.data.database.dao.ShoppingListDao

@Database(
    entities = [ShoppingItem::class, ShoppingList::class],
    version = 1, exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao

    abstract fun shoppingItemDao(): ShoppingItemDao

    companion object {
        @Volatile

//        ссылка на объект базы
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database_shopping"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}