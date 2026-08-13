package com.example.purchases.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PurchaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}