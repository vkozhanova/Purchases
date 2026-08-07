package com.example.purchases

import AllListsScreen
import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.purchases.repository.ShoppingRepository
import com.example.purchases.ui.EditListScreen
import com.example.purchases.ui.components.ShoppingList
import com.example.purchases.viewmodel.MainViewModel
import com.example.purchases.viewmodel.ShoppingListViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PurchaseApp(
    viewModel: MainViewModel,
    repository: ShoppingRepository,
) {
    var currentScreen by remember { mutableStateOf(ScreenId.ALL_LISTS) }
    var selectedList by remember { mutableStateOf<ShoppingList?>(null) }


    Scaffold { paddingValues ->
        when (currentScreen) {
            ScreenId.ALL_LISTS -> {
                AllListsScreen(
                    viewModel = viewModel,
                    onListClick = { list ->
                        selectedList = list
                        currentScreen = ScreenId.EDIT
                    },
                )
            }

            ScreenId.EDIT -> {
                val list = selectedList ?: run {
                    currentScreen = ScreenId.ALL_LISTS
                    return@Scaffold
                }
                val listViewModel = remember(list.id) {
                    ShoppingListViewModel(repository, list.id)
                }
                EditListScreen(
                    viewModel = listViewModel,
                    listName = list.name,
                    onBackClick = { currentScreen = ScreenId.ALL_LISTS }
                )
            }
        }
    }
}
