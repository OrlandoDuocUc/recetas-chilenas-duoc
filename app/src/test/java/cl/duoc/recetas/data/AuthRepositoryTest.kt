package cl.duoc.recetas.data

import cl.duoc.recetas.data.local.SessionManager
import cl.duoc.recetas.data.local.dao.UserDao
import cl.duoc.recetas.data.local.entities.UserEntity
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var repository: AuthRepository
    private lateinit var userDao: UserDao
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        userDao = mockk()
        sessionManager = mockk()
        repository = AuthRepository(userDao, sessionManager)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `login con credenciales correctas debe retornar usuario`() = runTest {
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

        coEvery { userDao.login(email, password) } returns expectedUser
        coEvery { sessionManager.saveSession(any(), any(), any(), any()) } just Runs

        // Act
        val result = repository.login(email, password)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        coVerify { sessionManager.saveSession(1, email, "Test User", "user") }
    }

    @Test
    fun `login con credenciales incorrectas debe retornar error`() = runTest {
        // Arrange
        val email = "wrong@test.com"
        val password = "wrong"

        coEvery { userDao.login(email, password) } returns null

        // Act
        val result = repository.login(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Credenciales incorrectas", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register con email nuevo debe crear usuario`() = runTest {
        // Arrange
        val email = "new@test.com"
        val password = "123456"
        val name = "New User"

        coEvery { userDao.getUserByEmail(email) } returns null
        coEvery { userDao.insert(any()) } returns 1L
        coEvery { sessionManager.saveSession(any(), any(), any(), any()) } just Runs

        // Act
        val result = repository.register(email, password, name)

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals(email, user?.email)
        assertEquals("user", user?.role)
    }

    @Test
    fun `register con email existente debe retornar error`() = runTest {
        // Arrange
        val email = "existing@test.com"
        val existingUser = UserEntity(
            id = 1,
            email = email,
            password = "123456",
            name = "Existing User",
            role = "user"
        )

        coEvery { userDao.getUserByEmail(email) } returns existingUser

        // Act
        val result = repository.register(email, "newpass", "New Name")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("El email ya está registrado", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateProfile debe actualizar datos correctamente`() = runTest {
        // Arrange
        val userId = 1
        val newName = "Updated Name"
        val existingUser = UserEntity(
            id = userId,
            email = "test@test.com",
            password = "oldpass",
            name = "Old Name",
            role = "user"
        )

        coEvery { userDao.getUserById(userId) } returns existingUser
        coEvery { userDao.update(any()) } just Runs
        coEvery { sessionManager.saveSession(any(), any(), any(), any()) } just Runs

        // Act
        val result = repository.updateProfile(userId, newName, null)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userDao.update(match { it.name == newName && it.password == "oldpass" }) }
    }

    @Test
    fun `getAllUsers debe retornar lista de usuarios`() = runTest {
        // Arrange
        val users = listOf(
            UserEntity(1, "user1@test.com", "pass", "User 1", "user"),
            UserEntity(2, "user2@test.com", "pass", "User 2", "user")
        )

        every { userDao.getAllUsers() } returns flowOf(users)

        // Act
        var result: List<UserEntity>? = null
        repository.getAllUsers().collect { result = it }

        // Assert
        assertNotNull(result)
        assertEquals(2, result?.size)
    }

    @Test
    fun `deleteUser debe eliminar usuario correctamente`() = runTest {
        // Arrange
        val userId = 1
        coEvery { userDao.deleteUser(userId) } just Runs

        // Act
        val result = repository.deleteUser(userId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userDao.deleteUser(userId) }
    }

    @Test
    fun `logout debe limpiar sesión`() = runTest {
        // Arrange
        coEvery { sessionManager.clearSession() } just Runs

        // Act
        repository.logout()

        // Assert
        coVerify { sessionManager.clearSession() }
    }
}
