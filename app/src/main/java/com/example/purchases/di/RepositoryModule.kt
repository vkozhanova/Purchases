package com.example.purchases.di

import com.example.purchases.data.repository.ShoppingRepository
import com.example.purchases.data.repository.ShoppingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindShoppingRepository(
        repository: ShoppingRepositoryImpl
    ): ShoppingRepository
}