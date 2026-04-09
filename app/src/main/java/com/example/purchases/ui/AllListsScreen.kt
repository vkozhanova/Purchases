package com.example.purchases.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.purchases.ui.components.ShoppingList
import com.example.purchases.viewmodel.MainViewModel
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.tooling.preview.Preview
@Composable
fun AllListsScreen(
    viewModel: MainViewModel,
    onListClick: (ShoppingList) -> Unit
) {
    val lists by viewModel.shoppingList.observeAsState(emptyList())

    AllListsScreenContent(
        lists = lists,
        onAddClick = { viewModel.createNewList()},
        onListClick = onListClick,
        onDeleteClick = { viewModel.deleteList(it) },
        onCopyClick = { viewModel.copyList(it) }
    )
}

@Composable
fun AllListsScreenContent(
    lists: List<ShoppingList>,
    onAddClick: () -> Unit,
    onListClick: (ShoppingList) -> Unit,
    onDeleteClick: (ShoppingList) -> Unit,
    onCopyClick: (ShoppingList) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick )
            {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        if (lists.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {
                items(lists) { list ->
                    ShoppingListItem(
                        shoppingList = list,
                        onClick = { onListClick(list) },
                        onDelete = { onDeleteClick(list) },
                        onCopy = { onCopyClick(list) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = "No items available",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun ShoppingListItem(
    shoppingList: ShoppingList,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
) {
    var menuExpanded by remember{mutableStateOf(false)}

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = { menuExpanded = true })
    ) {
        Column {
            Text(text = shoppingList.name)
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {menuExpanded = false}
            ) {
                DropdownMenuItem(
                    text = {Text("Удалить")},
                    onClick = {
                        onDelete()
                        menuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {Text("Копировать")},
                    onClick = {
                        onCopy()
                        menuExpanded = false
                    }
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllListScreenPreview() {
    val shoppingLists = listOf(
        ShoppingList(0, "00"),
        ShoppingList(1, "01"),
        ShoppingList(2, "02"),
        ShoppingList(3, "03"),

        )
    AllListsScreenContent(
        lists = shoppingLists,
        onAddClick = {},
        onListClick = {},
        onDeleteClick = {},
        onCopyClick = {}
    )
}
