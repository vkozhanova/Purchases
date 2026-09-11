package com.example.purchases.features.lists.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.purchases.features.lists.presentation.model.ShoppingListsUiState
import com.example.purchases.data.database.entity.ShoppingList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllListsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testLists = listOf(
        ShoppingList(id = 1, name = "Продукты"),
        ShoppingList(id = 2, name = "Одежда"),
        ShoppingList(id = 3, name = "Электроника")
    )

    private fun setContent(
        uiState: ShoppingListsUiState = ShoppingListsUiState(lists = testLists),
        searchQuery: String = "",
        filteredLists: List<ShoppingList> = uiState.lists,
        onSearchQueryChange: (String) -> Unit = {},
        onAddClick: () -> Unit = {},
        onListClick: (ShoppingList) -> Unit = {},
        onDeleteClick: (ShoppingList) -> Unit = {},
        onCopyClick: (ShoppingList) -> Unit = {},
        onRename: (ShoppingList, String) -> Unit = { _, _ -> }
    ) {
        composeTestRule.setContent {
            AllListsScreenContent(
                uiState = uiState,
                searchQuery = searchQuery,
                filteredLists = filteredLists,
                onSearchQueryChange = onSearchQueryChange,
                onAddClick = onAddClick,
                onListClick = onListClick,
                onDeleteClick = onDeleteClick,
                onCopyClick = onCopyClick,
                onRename = onRename
            )
        }
    }

    @Test
    fun when_loading_shows_progress_indicator() {
        setContent(uiState = ShoppingListsUiState(isLoading = true))
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun when_error_shows_error_message() {
        val errorMessage = "Network error"
        setContent(uiState = ShoppingListsUiState(error = errorMessage))
        composeTestRule.onNodeWithTag("error_message").assertIsDisplayed()
    }

    @Test
    fun when_empty_lists_shows_empty_state() {
        setContent(uiState = ShoppingListsUiState(lists = emptyList()))
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }

    @Test
    fun click_on_list_item_calls_onListClick() {
        var clickedList: ShoppingList? = null
        setContent(
            onListClick = { clickedList = it }
        )
        composeTestRule.onNodeWithTag("list_item_1").performClick()
        assert(clickedList == testLists.first { it.id == 1 })
    }

    @Test
    fun long_click_opens_context_menu() {
        setContent()
        composeTestRule.onNodeWithTag("list_item_1").performTouchInput { longClick() }
        composeTestRule.onNodeWithTag("context_menu_1").assertIsDisplayed()
    }

    @Test
    fun delete_from_context_menu_calls_onDeleteClick() {
        var deletedList: ShoppingList? = null
        setContent(
            onDeleteClick = { deletedList = it }
        )
        composeTestRule.onNodeWithTag("list_item_1").performTouchInput { longClick() }
        composeTestRule.onNodeWithTag("delete_menu_item_1").performClick()
        assert(deletedList == testLists.first { it.id == 1 })
    }

    @Test
    fun copy_from_context_menu_calls_onCopyClick() {
        var copiedList: ShoppingList? = null
        setContent(
            onCopyClick = { copiedList = it }
        )
        composeTestRule.onNodeWithTag("list_item_1").performTouchInput { longClick() }
        composeTestRule.onNodeWithTag("copy_menu_item_1").performClick()
        assert(copiedList == testLists.first { it.id == 1 })
    }

    @Test
    fun edit_icon_opens_rename_dialog() {
        setContent()
        composeTestRule.onNodeWithTag("edit_button_1").performClick()
        composeTestRule.onNodeWithTag("rename_text_field_1").assertIsDisplayed()
    }

    @Test
    fun rename_dialog_save_calls_onRename_with_new_name() {
        var renamedList: ShoppingList? = null
        var newName: String? = null
        setContent(
            onRename = { list, name ->
                renamedList = list
                newName = name
            }
        )
        composeTestRule.onNodeWithTag("edit_button_1").performClick()
        composeTestRule.onNodeWithTag("rename_text_field_1").performClick()
        composeTestRule.onNodeWithTag("rename_text_field_1").performTextInput("Новое имя")
        composeTestRule.onNodeWithTag("rename_save_button_1").performClick()
        assert(renamedList == testLists.first { it.id == 1 })
        assert(newName?.contains("Новое имя") == true)
    }

    @Test
    fun rename_dialog_cancel_does_not_call_onRename() {
        var renameCalled = false
        setContent(
            onRename = { _, _ -> renameCalled = true }
        )
        composeTestRule.onNodeWithTag("edit_button_1").performClick()
        composeTestRule.onNodeWithTag("rename_cancel_button_1").performClick()
        composeTestRule.onNodeWithTag("rename_dialog_1").assertDoesNotExist()
        assert(!renameCalled)
    }

    @Test
    fun search_icon_shows_search_field() {
        setContent()
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_field").assertIsDisplayed()
    }

    @Test
    fun search_filters_lists() {
        composeTestRule.setContent {
            var searchQuery by remember { mutableStateOf("") }
            val filteredLists = testLists.filter { it.name.contains(searchQuery, ignoreCase = true) }
            AllListsScreenContent(
                uiState = ShoppingListsUiState(lists = testLists),
                searchQuery = searchQuery,
                filteredLists = filteredLists,
                onSearchQueryChange = { searchQuery = it },
                onAddClick = {},
                onListClick = {},
                onDeleteClick = {},
                onCopyClick = {},
                onRename = { _, _ -> }
            )
        }
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_field").performClick()
        composeTestRule.onNodeWithTag("search_field").performTextInput("Продукты")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("list_item_name_1", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("list_item_name_2", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithTag("list_item_name_3", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun fab_button_calls_onAddClick() {
        var addClicked = false
        setContent(onAddClick = { addClicked = true })
        composeTestRule.onNodeWithTag("fab_add").performClick()
        assert(addClicked)
    }

    @Test
    fun scroll_up_button_is_displayed_when_lists_not_empty() {
        setContent()
        composeTestRule.onNodeWithTag("scroll_up_button").assertIsDisplayed()
    }

    @Test
    fun scroll_down_button_is_displayed_when_lists_not_empty() {
        setContent()
        composeTestRule.onNodeWithTag("scroll_down_button").assertIsDisplayed()
    }

    @Test
    fun scroll_buttons_are_not_displayed_when_lists_empty() {
        setContent(uiState = ShoppingListsUiState(lists = emptyList()))
        composeTestRule.onNodeWithTag("scroll_up_button").assertDoesNotExist()
        composeTestRule.onNodeWithTag("scroll_down_button").assertDoesNotExist()
    }
}