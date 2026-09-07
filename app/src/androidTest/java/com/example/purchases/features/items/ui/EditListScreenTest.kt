package com.example.purchases.features.items.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.purchases.data.database.entity.ShoppingItem
import com.example.purchases.features.items.presentation.model.ShoppingItemsUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testItems = listOf(
        ShoppingItem(id = 1, listId = 1, name = "Молоко", isChecked = false),
        ShoppingItem(id = 2, listId = 1, name = "Хлеб", isChecked = true),
        ShoppingItem(id = 3, listId = 1, name = "Яйца", isChecked = false)
    )

    private fun setContent(
        uiState: ShoppingItemsUiState = ShoppingItemsUiState(items = testItems),
        listName: String = "Тестовый список",
        onBackClick: () -> Unit = {},
        onAddClick: () -> Unit = {},
        onDeleteClick: (ShoppingItem) -> Unit = {},
        onCopyClick: (ShoppingItem) -> Unit = {},
        onRename: (ShoppingItem, String) -> Unit = { _, _ -> },
        onToggleChecked: (ShoppingItem) -> Unit = {}
    ) {
        composeTestRule.setContent {
            EditListScreenContent(
                uiState = uiState,
                listName = listName,
                onBackClick = onBackClick,
                onAddClick = onAddClick,
                onDeleteClick = onDeleteClick,
                onCopyClick = onCopyClick,
                onRename = onRename,
                onToggleChecked = onToggleChecked
            )
        }
    }

    @Test
    fun when_loading_shows_indicator() {
        setContent(uiState = ShoppingItemsUiState(isLoading = true))
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun when_error_shows_error_message() {
        val error = "Ошибка загрузки"
        setContent(uiState = ShoppingItemsUiState(error = error))
        composeTestRule.onNodeWithTag("error_message").assertIsDisplayed()
    }

    @Test
    fun when_empty_shows_empty_state() {
        setContent(uiState = ShoppingItemsUiState(items = emptyList()))
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }

    @Test
    fun when_items_exist_they_are_displayed() {
        setContent()
        testItems.forEach { item ->
            composeTestRule.onNodeWithTag("item_row_${item.id}").assertIsDisplayed()
        }
    }

    @Test
    fun toggle_checkbox_calls_onToggleChecked() {
        var toggledItem: ShoppingItem? = null
        setContent(onToggleChecked = { toggledItem = it })

        composeTestRule.onNodeWithTag("checkbox_1").performClick()
        assert(toggledItem == testItems.first { it.id == 1 })
    }

    @Test
    fun edit_icon_opens_rename_dialog() {
        setContent()
        composeTestRule.onNodeWithTag("edit_button_1").performClick()
        composeTestRule.onNodeWithTag("rename_dialog").assertIsDisplayed()
    }

    @Test
    fun rename_dialog_save_calls_onRename() {
        var renameItem: ShoppingItem? = null
        var newName: String? = null
        setContent(
            onRename = { item, name ->
                renameItem = item
                newName = name
            })
        composeTestRule.onNodeWithTag("edit_button_1").performClick()
        composeTestRule.onNodeWithTag("rename_text_field").performClick()
        composeTestRule.onNodeWithTag("rename_text_field").performTextInput("Новое название")
        composeTestRule.onNodeWithTag("rename_save_button").performClick()
        assert(renameItem == testItems.first { it.id == 1 })
        assert(newName?.contains("Новое название") == true)
    }

    @Test
    fun click_on_row_opens_context_menu() {
        setContent()
        composeTestRule.onNodeWithTag("item_row_1").performClick()
        composeTestRule.onNodeWithTag("context_menu_1").assertIsDisplayed()
    }

    @Test
    fun delete_from_context_menu_calls_onDeleteClick() {
        var deletedItem: ShoppingItem? = null
        setContent(onDeleteClick = { deletedItem = it })

        composeTestRule.onNodeWithTag("item_row_1").performClick()
        composeTestRule.onNodeWithTag("delete_menu_item_1").performClick()
        assert(deletedItem == testItems.first { it.id == 1 })
    }

    @Test
    fun copy_from_context_menu_calls_onCopyClick() {
        var copiedItem: ShoppingItem? = null
        setContent(onCopyClick = {copiedItem = it})

        composeTestRule.onNodeWithTag("item_row_1").performClick()
        composeTestRule.onNodeWithTag("copy_menu_item_1").performClick()
        assert(copiedItem == testItems.first {it.id == 1})
    }

    @Test
    fun add_button_calls_onAddClick() {
        var  addClicked = false

        setContent(onAddClick = {addClicked = true})
        composeTestRule.onNodeWithTag("add_button").performClick()
        assert(addClicked)
    }

    @Test
    fun scroll_up_button_is_displayed_when_items_not_empty() {
        setContent()
        composeTestRule.onNodeWithTag("scroll_up_button").assertIsDisplayed()
    }

    @Test
    fun scroll_down_button_is_displayed_when_items_not_empty() {
        setContent()
        composeTestRule.onNodeWithTag("scroll_down_button").assertIsDisplayed()
    }

}