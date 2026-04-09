package com.example.purchases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.purchases.repository.AppDatabase
import com.example.purchases.repository.ShoppingRepository
import com.example.purchases.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val database = AppDatabase.getDatabase(this)
        val repository = ShoppingRepository(
            itemDao = database.shoppingItemDao(),
            listDao = database.shoppingListDao()
        )

        val viewModel = MainViewModel(repository)

        setContent {
            PurchaseApp(viewModel = viewModel)
        }
    }
}