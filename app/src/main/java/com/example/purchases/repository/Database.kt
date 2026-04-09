package com.example.purchases.repository

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import com.example.purchases.ui.components.ShoppingItem
import com.example.purchases.data.model.ShoppingItemDao
import com.example.purchases.ui.components.ShoppingList
import com.example.purchases.data.model.ShoppingListDao

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

