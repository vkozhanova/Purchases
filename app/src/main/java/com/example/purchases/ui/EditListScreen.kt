package com.example.purchases.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.purchases.ShoppingItemsUiState
import com.example.purchases.ui.components.ShoppingItem
import com.example.purchases.viewmodel.ShoppingListViewModel

@Composable
fun EditListScreen(
    viewModel: ShoppingListViewModel,
    listName: String,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    EditListScreenContent(
        uiState = uiState,
        listName = listName,
        onBackClick = onBackClick,
        onAddClick = { viewModel.addItem("") },
        onDeleteClick = { viewModel.deleteItem(it) },
        onCopyClick = { viewModel.copyItem(it) },
        onRename = { item, newName -> viewModel.updateItem(item.copy(name = newName)) },
        onToggleChecked = { viewModel.toggleChecked(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListScreenContent(
    uiState: ShoppingItemsUiState,
    listName: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (ShoppingItem) -> Unit,
    onCopyClick: (ShoppingItem) -> Unit,
    onRename: (ShoppingItem, String) -> Unit,
    onToggleChecked: (ShoppingItem) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = listName,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Добавить")
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

            uiState.items.isEmpty() -> {
                EmptyState(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding() + 20.dp,
                            start = 20.dp,
                            end = 20.dp
                        )
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.items,
                            key = { _, item -> item.id }
                        ) { index, item ->
                            ShoppingItemRow(
                                item = item,
                                index = index + 1,
                                onToggleChecked = { onToggleChecked(item) },
                                onDelete = { onDeleteClick(item) },
                                onCopy = { onCopyClick(item) },
                                onRename = { newName -> onRename(item, newName) }
                            )
                            if (index < uiState.items.size - 1) {
                                Divider(
                                    color = MaterialTheme.colorScheme.surface,
                                    thickness = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
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
            text = "Список еще пуст",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    index: Int,
    onToggleChecked: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onRename: (String) -> Unit,
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(item.name) }
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (item.isChecked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .align(Alignment.Center)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { menuExpanded = true }
                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index}.",
                style = purchaseAppTypography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .width(20.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = item.name,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onToggleChecked() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.background
                )
            )
            IconButton(
                onClick = { showRenameDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { menuExpanded = false },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        DropdownMenuItem(
            text = { Text("Удалить") },
            onClick = {
                onDelete()
                menuExpanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("Копировать") },
            onClick = {
                onCopy()
                menuExpanded = false
            }
        )
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text(text = "Изменить название") },
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
                        if (newName.isNotBlank()) {
                            onRename(newName)
                        }
                        showRenameDialog = false
                    },
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRenameDialog = false }) {
                    Text(text = "Отмена")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditListScreenPreview() {
    PurchaseAppTheme {
        val items = listOf(
            ShoppingItem(0, 1, "Уборка", false),
            ShoppingItem(1, 1, "Продукты", true),
            ShoppingItem(2, 1, "", true),
            ShoppingItem(3, 1, "Стоматолог", false)
        )
        EditListScreenContent(
            uiState = ShoppingItemsUiState(items = items),
            listName = "Список дел вторник",
            onBackClick = { },
            onAddClick = { },
            onDeleteClick = { },
            onCopyClick = { },
            onRename = { _, _ -> },
            onToggleChecked = { }
        )
    }
}