package com.example.purchases.features.lists.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.purchases.features.ui.components.ShoppingList
import com.example.purchases.features.lists.presentation.MainViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.example.purchases.features.lists.presentation.model.ShoppingListsUiState
import com.example.purchases.features.ui.PurchaseAppTheme
import com.example.purchases.features.ui.purchaseAppTypography

@Composable
fun AllListsScreen(
    viewModel: MainViewModel,
    onListClick: (ShoppingList) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    AllListsScreenContent(
        uiState = uiState,
        onAddClick = { viewModel.createNewList() },
        onListClick = onListClick,
        onDeleteClick = { viewModel.deleteList(it) },
        onCopyClick = { viewModel.copyList(it) },
        onRename = { list, newName -> viewModel.renameList(list, newName) }
    )
}

@Composable
fun AllListsScreenContent(
    uiState: ShoppingListsUiState,
    onAddClick: () -> Unit,
    onListClick: (ShoppingList) -> Unit,
    onDeleteClick: (ShoppingList) -> Unit,
    onCopyClick: (ShoppingList) -> Unit,
    onRename: (ShoppingList, String) -> Unit,
) {
    val listState = rememberLazyListState()
    var previousSize by remember { mutableStateOf(uiState.lists.size) }

    LaunchedEffect(uiState.lists.size) {
        val currentSize = uiState.lists.size
        if (currentSize > previousSize && currentSize > 0) {
            listState.animateScrollToItem(index = currentSize - 1)
        }
        previousSize = currentSize
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка: ${uiState.error}")
                }
            }

            uiState.lists.isEmpty() -> {
                EmptyState(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = 0.dp
                    ),
                    contentPadding = PaddingValues(bottom = 160.dp)
                ) {
                    items(uiState.lists) { list ->
                        ShoppingListItem(
                            shoppingList = list,
                            onClick = { onListClick(list) },
                            onDelete = { onDeleteClick(list) },
                            onCopy = { onCopyClick(list) },
                            onRename = { newName -> onRename(list, newName) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = "Нет доступных списков",
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
    onRename: (String) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(shoppingList.name) }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = {menuExpanded = true}
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(4.dp)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shoppingList.name,
                style = purchaseAppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { showRenameDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать название",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraSmall
            ),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 1.dp,
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Удалить",
                        style = purchaseAppTypography.titleMedium,
                    )
                },
                onClick = {
                    onDelete()
                    menuExpanded = false
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.primary
                )
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "Копировать",
                        style = purchaseAppTypography.titleMedium,
                    )
                },
                onClick = {
                    onCopy()
                    menuExpanded = false
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        if (showRenameDialog) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                containerColor = MaterialTheme.colorScheme.background,
                textContentColor = MaterialTheme.colorScheme.background,
                title = { Text("Изменить название") },
                text = {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onRename(newName)
                            showRenameDialog = false
                        }
                    ) {
                        Text("Сохранить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllListScreenPreview() {
    PurchaseAppTheme {
        val shoppingLists = listOf(
            ShoppingList(0, "Продукты"),
            ShoppingList(1, ""),
            ShoppingList(2, "02"),
            ShoppingList(3, "03"),
            )
        AllListsScreenContent(
            uiState = ShoppingListsUiState(lists = shoppingLists),
            onAddClick = {},
            onListClick = {},
            onDeleteClick = {},
            onCopyClick = {},
            onRename = { _, _ -> }
        )
    }
}