package com.example.purchases

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.purchases.ui.AllListsScreen
import com.example.purchases.ui.EditListScreen
import com.example.purchases.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PurchaseApp(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(ScreenId.ALL_LISTS) }
    var selectedListId by remember { mutableStateOf<Int?>(null) }


    Scaffold { paddingValues ->
        when (currentScreen) {
            ScreenId.ALL_LISTS -> {
                AllListsScreen(
                    viewModel = viewModel,
                    onListClick = { list -> selectedListId = list.id },
                )
            }

            ScreenId.EDIT -> {
                val listId = selectedListId ?: run {
                    currentScreen = ScreenId.ALL_LISTS
                    return@Scaffold
                }
                EditListScreen(
                    listId = listId,
                    onBackClick = { currentScreen = ScreenId.ALL_LISTS }
                )
            }
        }
    }
}
