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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredLists by viewModel.filteredLists.collectAsState()

    val onAddClick = remember { { viewModel.createNewList() } }
    val onADelete = remember { { list: ShoppingList -> viewModel.deleteList(list) } }
    val onCopyClick = remember { { list: ShoppingList -> viewModel.copyList(list) } }
    val onRename =
        remember { { list: ShoppingList, newName: String -> viewModel.renameList(list, newName) } }


    AllListsScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        filteredLists = filteredLists,
        onSearchQueryChange = viewModel::updateSearchQuery,
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
    searchQuery: String,
    filteredLists: List<ShoppingList>,
    onSearchQueryChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onListClick: (ShoppingList) -> Unit,
    onDeleteClick: (ShoppingList) -> Unit,
    onCopyClick: (ShoppingList) -> Unit,
    onRename: (ShoppingList, String) -> Unit,
) {

    val listState = rememberLazyListState()
    var previousSize by remember { mutableIntStateOf(uiState.lists.size) }
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

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
                    IconButton(onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("search_button")) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                    if (uiState.lists.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            modifier = Modifier.testTag("scroll_up_button")
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
                            },
                            modifier = Modifier.testTag("scroll_down_button")
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
                contentColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.testTag("fab_add")
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
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search_string)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") },
                                modifier = Modifier.testTag("clear_search_button")) {
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

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,

                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("search_field")
                )
            }
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.testTag("loading_indicator")
                        )
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ошибка: ${uiState.error}",
                            modifier = Modifier.testTag("error_message"))
                    }
                }

                uiState.lists.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .testTag("empty_state"),
                        message = if (searchQuery.isNotEmpty()) stringResource(R.string.nothing_find) else stringResource(R.string.no_lists)
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("lists_container"),
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
            )
            .testTag("list_item_${shoppingList.id}"),
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("list_item_name_${shoppingList.id}")
            )
            IconButton(
                onClick = { showRenameDialog = true },
                modifier = Modifier.testTag("edit_button_${shoppingList.id}")
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
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .testTag("context_menu_${shoppingList.id}"),
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
                modifier = Modifier.testTag("delete_menu_item_${shoppingList.id}"),
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
                modifier = Modifier.testTag("copy_menu_item_${shoppingList.id}"),
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
                        modifier = Modifier.testTag("rename_text_field_${shoppingList.id}"),
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
                        },
                        modifier = Modifier.testTag("rename_save_button_${shoppingList.id}")
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false },
                        modifier = Modifier.testTag("rename_cancel_button_${shoppingList.id}")) {
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
        val sampleLists = listOf(
            ShoppingList(1, "Продукты"),
            ShoppingList(2, "Хлеб")
        )
        AllListsScreenContent(
            uiState = ShoppingListsUiState(
                isLoading = false,
                lists = sampleLists
            ),
            searchQuery = "",
            filteredLists = sampleLists,
            onSearchQueryChange = { },
            onAddClick = {},
            onListClick = {},
            onDeleteClick = {},
            onCopyClick = {},
            onRename = { _, _ -> }
        )
    }
}