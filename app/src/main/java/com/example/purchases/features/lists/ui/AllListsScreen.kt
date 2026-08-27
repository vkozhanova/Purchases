package com.example.purchases.features.lists.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.purchases.R
import com.example.purchases.features.lists.presentation.model.ShoppingListsUiState
import com.example.purchases.ui.theme.PurchaseAppTheme
import com.example.purchases.ui.theme.purchaseAppTypography
import kotlinx.coroutines.launch

@Composable
fun AllListsScreen(
    viewModel: MainViewModel,
    onListClick: (ShoppingList) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val onAddClick = remember { { viewModel.createNewList() } }
    val onADelete = remember { { list: ShoppingList -> viewModel.deleteList(list) } }
    val onCopyClick = remember { { list: ShoppingList -> viewModel.copyList(list) } }
    val onRename =
        remember { { list: ShoppingList, newName: String -> viewModel.renameList(list, newName) } }


    AllListsScreenContent(
        uiState = uiState,
        onAddClick = onAddClick,
        onListClick = onListClick,
        onDeleteClick = onADelete,
        onCopyClick = onCopyClick,
        onRename = onRename
    )
}

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllListsScreenContent(
    uiState: ShoppingListsUiState,
    onAddClick: () -> Unit,
    onListClick: (ShoppingList) -> Unit,
    onDeleteClick: (ShoppingList) -> Unit,
    onCopyClick: (ShoppingList) -> Unit,
    onRename: (ShoppingList, String) -> Unit,
) {
    val viewModel: MainViewModel = hiltViewModel()
    val listState = rememberLazyListState()
    var previousSize by remember { mutableIntStateOf(uiState.lists.size) }
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredLists by viewModel.filteredLists.collectAsState()

    LaunchedEffect(uiState.lists.size) {
        val currentSize = uiState.lists.size
        if (currentSize > previousSize && currentSize > 0) {
            listState.animateScrollToItem(index = currentSize - 1)
        }
        previousSize = currentSize
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                    if (uiState.lists.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.to_up)
                            )
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(uiState.lists.size - 1)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.to_down)
                            )
                        }
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
                Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.add))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AnimatedVisibility(visible = expanded) {
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    placeholder = { Text(stringResource(R.string.search_string)) },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clean))
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        disabledContainerColor = MaterialTheme.colorScheme.background,

                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        disabledTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),

                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),

                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),

                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
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
                            .fillMaxSize(),
                        message = if (searchQuery.isNotEmpty()) stringResource(R.string.nothing_find) else stringResource(R.string.no_lists)
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 160.dp)
                    ) {
                        items(
                            items = filteredLists,
                            key = { it.id }
                        ) { list ->
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
}


@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.no_lists)
) {
    Box(modifier = modifier) {
        Text(
            text = message,
            style = purchaseAppTypography.bodyMedium,
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
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = { menuExpanded = true }
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
                .padding(10.dp),
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
                    contentDescription = stringResource(R.string.edit_title),
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
                        text = stringResource(R.string.to_delete),
                        style = purchaseAppTypography.bodySmall,
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
                        text = stringResource(R.string.to_copy),
                        style = purchaseAppTypography.bodySmall,
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
                title = { Text(stringResource(R.string.edit_title)) },
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
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun AllListScreenPreview() {
    PurchaseAppTheme {
        AllListsScreenContent(
            uiState = ShoppingListsUiState(
                isLoading = false,
                lists = listOf(
                    ShoppingList(1, "Продукты"),
                    ShoppingList(2, "Хлеб")
                )
            ),
            onAddClick = {},
            onListClick = {},
            onDeleteClick = {},
            onCopyClick = {},
            onRename = { _, _ -> }
        )
    }
}