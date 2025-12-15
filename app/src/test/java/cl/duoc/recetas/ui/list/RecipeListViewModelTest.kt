package cl.duoc.recetas.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cl.duoc.recetas.data.RecetasRepository
import cl.duoc.recetas.model.Recipe
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: RecipeListViewModel
    private lateinit var repository: RecetasRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = RecipeListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loadRecetas exitoso debe actualizar estado con lista de recetas`() = runTest {
        // Arrange
        val recipes = listOf(
            Recipe(id = 1, name = "Cazuela", description = "Deliciosa"),
            Recipe(id = 2, name = "Empanadas", description = "Ricas")
        )

        coEvery { repository.getRecetas() } returns Result.success(recipes)

        // Act
        viewModel.loadRecetas()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.data.size)
        assertEquals("Cazuela", state.data[0].name)
    }

    @Test
    fun `loadRecetas fallido debe actualizar estado con error`() = runTest {
        // Arrange
        val errorMessage = "Error de red"
        coEvery { repository.getRecetas() } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.loadRecetas()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.data.isEmpty())
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `toggleFavorite debe actualizar estado de favorito en la lista`() = runTest {
        // Arrange
        val recipe = Recipe(id = 1, name = "Cazuela", description = "Test", isFavorite = false)
        val recipes = listOf(recipe)

        coEvery { repository.getRecetas() } returns Result.success(recipes)
        every { repository.toggleFavorite(1, true) } just Runs

        viewModel.loadRecetas()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.toggleFavorite(recipe)

        // Assert
        val state = viewModel.state.value
        assertTrue(state.data[0].isFavorite)
        verify { repository.toggleFavorite(1, true) }
    }

    @Test
    fun `toggleFavorite de receta favorita debe quitarlo de favoritos`() = runTest {
        // Arrange
        val recipe = Recipe(id = 1, name = "Cazuela", description = "Test", isFavorite = true)
        val recipes = listOf(recipe)

        coEvery { repository.getRecetas() } returns Result.success(recipes)
        every { repository.toggleFavorite(1, false) } just Runs

        viewModel.loadRecetas()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.toggleFavorite(recipe)

        // Assert
        val state = viewModel.state.value
        assertFalse(state.data[0].isFavorite)
        verify { repository.toggleFavorite(1, false) }
    }

    @Test
    fun `init debe cargar recetas automáticamente`() = runTest {
        // Arrange
        val recipes = listOf(Recipe(id = 1, name = "Test", description = "Test"))
        coEvery { repository.getRecetas() } returns Result.success(recipes)

        // Act (el init ya se llamó en setup)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertEquals(1, state.data.size)
    }
}
