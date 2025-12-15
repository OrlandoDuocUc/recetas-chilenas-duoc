package cl.duoc.recetas.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cl.duoc.recetas.data.AuthRepository
import cl.duoc.recetas.data.local.entities.UserEntity
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
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `login exitoso debe actualizar estado con usuario`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "123456"
        val expectedUser = UserEntity(
            id = 1,
            email = email,
            password = password,
            name = "Test User",
            role = "user"
        )

        coEvery { repository.login(email, password) } returns Result.success(expectedUser)

        // Act
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(expectedUser, state.user)
    }

    @Test
    fun `login fallido debe actualizar estado con error`() = runTest {
        // Arrange
        val email = "wrong@test.com"
        val password = "wrong"
        val errorMessage = "Credenciales incorrectas"

        coEvery { repository.login(email, password) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        assertNull(state.user)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `register exitoso debe actualizar estado con usuario`() = runTest {
        // Arrange
        val email = "new@test.com"
        val password = "123456"
        val name = "New User"
        val expectedUser = UserEntity(
            id = 1,
            email = email,
            password = password,
            name = name,
            role = "user"
        )

        coEvery { repository.register(email, password, name) } returns Result.success(expectedUser)

        // Act
        viewModel.register(email, password, name)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(expectedUser, state.user)
    }

    @Test
    fun `register con email duplicado debe actualizar estado con error`() = runTest {
        // Arrange
        val email = "existing@test.com"
        val password = "123456"
        val name = "User"
        val errorMessage = "El email ya está registrado"

        coEvery { repository.register(email, password, name) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.register(email, password, name)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        assertNull(state.user)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `estado inicial debe tener isLoading en false`() {
        // Assert
        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        assertNull(state.user)
        assertNull(state.error)
    }
}
