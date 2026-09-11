package com.example.purchases.features.items.presentation

import com.example.purchases.data.database.entity.ShoppingItem
import com.example.purchases.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.flowOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModelTest {
    private lateinit var repository: ShoppingRepository
    private lateinit var viewModel: ShoppingItemsViewModel
    private val testListId = 1

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load items for listId`() = runTest {
        val items = listOf(
            ShoppingItem(id = 1, listId = testListId, name = "Хлеб"),
            ShoppingItem(id = 2, listId = testListId, name = "Молоко")
        )

        coEvery { repository.getItemsForList(testListId) } returns flowOf(items)

        viewModel = ShoppingItemsViewModel(repository, testListId)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(items, viewModel.uiState.value.items)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `init should set isLoading to true initially`() = runTest {
        coEvery{repository.getItemsForList(testListId)} returns flow{
            delay(1000.milliseconds)
            emit(emptyList())
        }

        viewModel = ShoppingItemsViewModel(repository, testListId)

        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init should handle error gracefully`() = runTest {
        val errorMessage = "Database error"
        coEvery { repository.getItemsForList(testListId) } returns flow {
            throw Exception(errorMessage)
        }

        viewModel = ShoppingItemsViewModel(repository, testListId)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun `addItem should insert new item with correct listId`() = runTest {
        val itemName = "Новый айтем"
        coEvery { repository.getItemsForList(testListId) } returns flowOf(emptyList())
        coEvery { repository.insertItem(any()) } returns Unit

        viewModel = ShoppingItemsViewModel(repository, testListId)
        viewModel.addItem(itemName)
        advanceUntilIdle()

        coVerify{
            repository.insertItem(match {
                it.listId == testListId && it.name == itemName && it.id == 0
            })
        }
    }

    @Test
    fun `addItem with empty name should still call repository`() = runTest {
        coEvery { repository.getItemsForList(testListId) } returns flowOf(emptyList())
        coEvery { repository.insertItem(any()) } returns Unit

        viewModel = ShoppingItemsViewModel(repository, testListId)
        viewModel.addItem("")
        advanceUntilIdle()

        coVerify { repository.insertItem(any()) }
    }

    @Test
    fun `deleteItem should call repository deleteItem`() = runTest {
        val item = ShoppingItem(id = 1, listId = testListId, name = "Хлеб")
        coEvery { repository.getItemsForList(testListId) } returns flowOf(emptyList())
        coEvery { repository.deleteItem(item) } returns Unit

        viewModel =  ShoppingItemsViewModel(repository, testListId)
        viewModel.deleteItem(item)
        advanceUntilIdle()

        coVerify { repository.deleteItem(item) }
    }

    @Test
    fun `copyItem should create copy with suffix and reset id`() = runTest {
        val origItem = ShoppingItem(id = 1, listId = testListId, name = "Хлеб")
        coEvery { repository.getItemsForList(testListId) } returns flowOf(emptyList())
        coEvery { repository.insertItem(any()) } returns Unit

        viewModel =  ShoppingItemsViewModel(repository, testListId)
        viewModel.copyItem(origItem)
        advanceUntilIdle()

        coVerify { repository.insertItem(match {
            it.id == 0 && it.listId == testListId && it.name == "Хлеб (копия)"
        }) }
    }

    @Test
    fun `copyItem with empty name should handle correctly`() = runTest {
        val originalItem = ShoppingItem(id = 1, listId = testListId, name = "")
        coEvery { repository.getItemsForList(testListId) } returns flowOf(emptyList())
        coEvery { repository.insertItem(any()) } returns Unit

        viewModel = ShoppingItemsViewModel(repository, testListId)
        viewModel.copyItem(originalItem)
        advanceUntilIdle()

        coVerify {
            repository.insertItem(match {
                it.id == 0 && it.name == " (копия)"
            })
        }
    }

    @Test
    fun `toggleChecked should flip isChecked state`() = runTest {
        val item = ShoppingItem(id = 1, listId = testListId, name = "Хлеб", isChecked = false)
        val expectedItem = item.copy(isChecked = true)

        coEvery { repository.getItemsForList(testListId) } returns flowOf(emptyList())
        coEvery { repository.updateItem(any()) } returns Unit

        viewModel = ShoppingItemsViewModel(repository, testListId)
        viewModel.toggleChecked(item)
        advanceUntilIdle()

        coVerify { repository.updateItem(expectedItem) }
    }
}