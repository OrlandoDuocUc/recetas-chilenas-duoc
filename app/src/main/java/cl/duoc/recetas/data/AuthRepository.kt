package cl.duoc.recetas.data

import cl.duoc.recetas.data.local.SessionManager
import cl.duoc.recetas.data.local.dao.UserDao
import cl.duoc.recetas.data.local.entities.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    
    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.login(email, password)
            if (user != null) {
                sessionManager.saveSession(user.id, user.email, user.name, user.role)
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(email: String, password: String, name: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            // Verificar si el email ya existe
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                return@withContext Result.failure(Exception("El email ya está registrado"))
            }
            
            val newUser = UserEntity(
                email = email,
                password = password,
                name = name,
                role = "user" // Por defecto es usuario normal
            )
            
            val userId = userDao.insert(newUser)
            val insertedUser = newUser.copy(id = userId.toInt())
            sessionManager.saveSession(insertedUser.id, insertedUser.email, insertedUser.name, insertedUser.role)
            
            Result.success(insertedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(userId: Int, name: String, newPassword: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("Usuario no encontrado"))
            
            val updatedUser = user.copy(
                name = name,
                password = newPassword ?: user.password
            )
            
            userDao.update(updatedUser)
            sessionManager.saveSession(updatedUser.id, updatedUser.email, updatedUser.name, updatedUser.role)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        sessionManager.clearSession()
    }
    
    fun getUserId(): Flow<Int?> = sessionManager.userId
    fun getUserRole(): Flow<String?> = sessionManager.userRole
    fun getUserName(): Flow<String?> = sessionManager.userName
    
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    
    suspend fun deleteUser(userId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userDao.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        var currentUserId: Int? = null
        sessionManager.userId.collect { currentUserId = it }
        currentUserId?.let { userDao.getUserById(it) }
    }
}
