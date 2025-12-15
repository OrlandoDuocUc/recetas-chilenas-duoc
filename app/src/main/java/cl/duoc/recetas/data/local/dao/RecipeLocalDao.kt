package cl.duoc.recetas.data.local.dao

import androidx.room.*
import cl.duoc.recetas.data.local.entities.RecipeLocalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeLocalDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeLocalEntity): Long
    
    @Update
    suspend fun update(recipe: RecipeLocalEntity)
    
    @Delete
    suspend fun delete(recipe: RecipeLocalEntity)
    
    @Query("SELECT * FROM recipes_local WHERE userId = :userId ORDER BY createdAt DESC")
    fun getRecipesByUser(userId: Int): Flow<List<RecipeLocalEntity>>
    
    @Query("SELECT * FROM recipes_local ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeLocalEntity>>
    
    @Query("SELECT * FROM recipes_local WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecipeLocalEntity?
    
    @Query("SELECT * FROM recipes_local WHERE isSynced = 0")
    suspend fun getUnsyncedRecipes(): List<RecipeLocalEntity>
    
    @Query("UPDATE recipes_local SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)
}
