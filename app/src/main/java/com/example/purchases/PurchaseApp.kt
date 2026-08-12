package com.example.purchases

import com.example.purchases.features.lists.ui.AllListsScreen
import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.purchases.core.navigation.Destination
import com.example.purchases.data.database.repository.ShoppingRepository
import com.example.purchases.features.ui.EditListScreen
import com.example.purchases.features.lists.presentation.MainViewModel
import com.example.purchases.features.items.presentation.ShoppingListViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PurchaseApp(
    viewModel: MainViewModel,
    repository: ShoppingRepository,
) {
    var currentScreen by remember { mutableStateOf<Destination>(Destination.AllLists) }

    Scaffold { _ ->
        when (currentScreen) {
            is Destination.AllLists -> {
                AllListsScreen(
                    viewModel = viewModel,
                    onListClick = { list ->
                        currentScreen = Destination.Edit(list.id, list.name)
                    }
                )
            }

            is Destination.Edit -> {
                val listId = (currentScreen as Destination.Edit).listId
                val listName = (currentScreen as Destination.Edit).listName

                val listViewModel = remember(listId) {
                    ShoppingListViewModel(repository, listId)
                }
                EditListScreen(
                    viewModel = listViewModel,
                    listName = listName,
                    onBackClick = { currentScreen = Destination.AllLists }
                )
            }
        }
    }
}