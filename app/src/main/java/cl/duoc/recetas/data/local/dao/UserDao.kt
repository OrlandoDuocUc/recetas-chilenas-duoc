package cl.duoc.recetas.data.local.dao

import androidx.room.*
import cl.duoc.recetas.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long
    
    @Update
    suspend fun update(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?
    
    @Query("SELECT * FROM users WHERE role = 'user'")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}
