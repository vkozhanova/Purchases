package com.example.purchases.features.lists.presentation

import com.example.purchases.data.database.entity.ShoppingItem
import com.example.purchases.data.repository.ShoppingRepository
import com.example.purchases.data.database.entity.ShoppingList
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private lateinit var repository: ShoppingRepository
    private lateinit var viewModel: AllListsViewModel

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
    fun `loadShoppingLists error should set error message`() = runTest {
        val errorMessage = "Network error"
        coEvery { repository.getAllLists() } returns flow {
            throw Exception(errorMessage)
        }

        viewModel = AllListsViewModel(repository)
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadShoppingLists success should update uiState`() = runTest {
        val mockLists = listOf(
            ShoppingList(id = 1, "Покупки"),
            ShoppingList(id = 2, "Покупки1")
        )

        coEvery { repository.getAllLists() } returns flowOf(mockLists)

        viewModel = AllListsViewModel(repository)
        advanceUntilIdle()

        assertEquals(mockLists, viewModel.allLists.value)
        assertEquals(mockLists, viewModel.uiState.value.lists)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `search query should filter lists`() = runTest {
        val mockLists = listOf(
            ShoppingList(id = 1, "Покупки"),
            ShoppingList(id = 2, "Покупки1"),
            ShoppingList(id = 3, "Новый список")
        )

        coEvery { repository.getAllLists() } returns flowOf(mockLists)
        viewModel = AllListsViewModel(repository)
        advanceUntilIdle()

        assertEquals(3, viewModel.allLists.value.size)
        viewModel.updateSearchQuery("покупки")

        val filtered = viewModel.filteredLists.first { it.size == 2 }
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.name.contains("покупки", ignoreCase = true) })
    }

    @Test
    fun `deleteList should call repository`() = runTest {
        val list = ShoppingList(id = 0, "Покупки")
        coEvery { repository.getAllLists() } returns flowOf(emptyList())
        coEvery { repository.deleteList(list) } returns Unit

        viewModel = AllListsViewModel(repository)
        viewModel.deleteList(list)

        coVerify { repository.deleteList(list) }
    }

    @Test
    fun `copyList should create copy with suffix`() = runTest {
        val originalList = ShoppingList(id = 1, "Покупки")
        val items = listOf(
            ShoppingItem(id = 1, listId = 1, name = "Хлеб"),
            ShoppingItem(id = 2, listId = 1, name = "Молоко")
        )

        coEvery { repository.getAllLists() } returns flowOf(emptyList())
        coEvery { repository.insertList(any()) } returns 2L
        coEvery { repository.getItemsForList(1) } returns flowOf(items)
        coEvery { repository.insertItem(any()) } returns Unit

        viewModel = AllListsViewModel(repository)
        viewModel.copyList(originalList)
        advanceUntilIdle()

        coVerify {
            repository.insertList(match {
                it.name == "Покупки (копия)" && it.id == 0
            })
        }
        coVerify(exactly = 2) { repository.insertItem(any()) }
    }

    @Test
    fun `createNewList should create list with default name`() = runTest {
        coEvery { repository.getAllLists() } returns flowOf(emptyList())
        coEvery { repository.insertList(any()) } returns 1L

        viewModel = AllListsViewModel(repository)
        viewModel.createNewList()

        coVerify {
            repository.insertList(match {
                it.name == "Новый список" && it.id == 0
            })
        }
    }

    @Test
    fun `renameList should update list name`() = runTest {
        val list = ShoppingList(id = 1, name = "Старое имя")
        val newName = "Новое имя"

        coEvery { repository.getAllLists() } returns flowOf(emptyList())
        coEvery { repository.insertList(any()) } returns 1L

        viewModel = AllListsViewModel(repository)
        viewModel.renameList(list, newName)

        coVerify {
            repository.insertList(match {
                it.id == 1 && it.name == newName
            })
        }
    }

    @Test
    fun `copyList with empty items should handle gracefully`() = runTest {
        val origList = ShoppingList(id = 1, name = "Пустой список")

        coEvery { repository.getAllLists() } returns flowOf(emptyList())
        coEvery { repository.insertList(any()) } returns 2
        coEvery { repository.getItemsForList(1) } returns flowOf(emptyList())

        viewModel = AllListsViewModel(repository)
        viewModel.copyList(origList)
        advanceUntilIdle()

        coVerify { repository.insertList(any()) }
        coVerify(exactly = 0) { repository.insertItem(any()) }
    }
}