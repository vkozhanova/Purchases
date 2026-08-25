package com.example.purchases.features.items.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.purchases.features.items.presentation.model.ShoppingItemsUiState
import com.example.purchases.data.database.entity.ShoppingItem
import com.example.purchases.domain.export.ImageExporter
import com.example.purchases.features.items.presentation.ShoppingListViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Divider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import com.example.purchases.domain.export.ExportStyle
import com.example.purchases.ui.theme.purchaseAppTypography

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

@Composable
fun ExportableContent(
    items: List<ShoppingItem>,
    listName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = listName,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${index + 1}. ${item.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                if (item.isChecked) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Green
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
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
    val context = LocalContext.current
    val imageExporter = remember { ImageExporter() }
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var previousSize by remember { mutableStateOf(uiState.items.size) }
    val snapFlingBehavior = rememberSnapFlingBehavior(listState)

    LaunchedEffect(uiState.items.size) {
        val currentSize = uiState.items.size
        when {
            currentSize > previousSize && currentSize > 0 -> {
                listState.animateScrollToItem(currentSize - 1)
            }

            previousSize == 0 && currentSize > 0 -> {
                listState.animateScrollToItem(0)
            }
        }
        previousSize = currentSize
    }

    val density = LocalDensity.current
    val exportStyle = ExportStyle(
        backgroundColor = MaterialTheme.colorScheme.background.toArgb(),
        titleColor = MaterialTheme.colorScheme.primary.toArgb(),
        titleTextSize = MaterialTheme.typography.headlineSmall.fontSize.value,
        itemColor = MaterialTheme.colorScheme.onBackground.toArgb(),
        checkedColor = MaterialTheme.colorScheme.primary.toArgb(),
        itemTextSize = MaterialTheme.typography.bodyLarge.fontSize.value,
        paddingPx = 60,
        itemHeightPx = 50,
        headerHeightPx = 100,
        dividerColor = MaterialTheme.colorScheme.outline.toArgb(),
        dividerStrokeWidth = 4f,
        imageWidth = 1080,
        strikeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f).toArgb(),
        strikeHeightPx = with(density) { 10.dp.toPx() }
    )

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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (uiState.items.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Вверх"
                            )
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(uiState.items.size - 1)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Вниз"
                            )
                        }
                    }
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Rounded.Add, contentDescription = "Добавить")
                    }
                    // Кнопка экспорта
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (uiState.items.isNotEmpty()) {
                                    try {
                                        val file = imageExporter.exportToImage(
                                            context = context,
                                            items = uiState.items,
                                            listName = listName,
                                            style = exportStyle
                                        )
                                       imageExporter.openImageForPreview(context, file)
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(
                                            message = "Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = "Список пуст",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Экспорт в PNG")
                    }
                }
            )
        }
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
                val configuration = LocalConfiguration.current
                val screenHeightDp = configuration.screenHeightDp.dp

                val topPadding = paddingValues.calculateTopPadding()
                val bottomPadding = paddingValues.calculateBottomPadding() + 8.dp

                val maxListHeight = screenHeightDp - topPadding - bottomPadding - 22.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = topPadding,
                            bottom = bottomPadding,
                            start = 20.dp,
                            end = 20.dp
                        )
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    LazyColumn(
                        state = listState,
                        flingBehavior = snapFlingBehavior,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = maxListHeight),
                        contentPadding = PaddingValues(top = 2.dp, bottom = 2.dp)
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
                                onRename = { newName -> onRename(item, newName) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (index < uiState.items.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.surface
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
    modifier: Modifier = Modifier,
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(item.name) }
    var menuExpanded by remember { mutableStateOf(false) }
    val onRowClick = remember { { menuExpanded = true } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
                .clickable(onClick = onRowClick)
                .padding(start = 16.dp, end = 4.dp, top = 0.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = index.toString(),
                style = purchaseAppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .width(20.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
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
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraSmall
            ),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Удалить",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = {
                    onDelete()
                    menuExpanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Копировать",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = {
                    onCopy()
                    menuExpanded = false
                }
            )
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            textContentColor = MaterialTheme.colorScheme.background,
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
    _root_ide_package_.com.example.purchases.features.ui.PurchaseAppTheme {
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