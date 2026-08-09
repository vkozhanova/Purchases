package com.example.purchases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.purchases.data.database.AppDatabase
import com.example.purchases.data.database.repository.ShoppingRepositoryImpl
import com.example.purchases.features.ui.PurchaseAppTheme
import com.example.purchases.features.lists.presentation.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val database = AppDatabase.getDatabase(this)
        val repository = ShoppingRepositoryImpl(
            itemDao = database.shoppingItemDao(),
            listDao = database.shoppingListDao()
        )

        val viewModel = MainViewModel(repository)

        setContent {
            PurchaseAppTheme {
                PurchaseApp(viewModel = viewModel, repository = repository)
            }
        }
    }
}