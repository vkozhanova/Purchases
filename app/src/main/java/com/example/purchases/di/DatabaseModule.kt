package com.example.purchases.di

import com.example.purchases.data.database.AppDatabase
import com.example.purchases.data.database.dao.ShoppingItemDao
import com.example.purchases.data.database.dao.ShoppingListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideShoppingListDao(database: AppDatabase): ShoppingListDao = database.shoppingListDao()

    @Provides
    fun provideShoppingItemDao(database: AppDatabase): ShoppingItemDao = database.shoppingItemDao()
}