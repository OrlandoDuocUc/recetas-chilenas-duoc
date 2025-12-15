package cl.duoc.recetas.data.local.dao

import androidx.room.*
import cl.duoc.recetas.data.local.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE recipeId = :recipeId AND userId = :userId")
    suspend fun delete(recipeId: Int, userId: Int)
    
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesByUser(userId: Int): Flow<List<FavoriteEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE recipeId = :recipeId AND userId = :userId)")
    suspend fun isFavorite(recipeId: Int, userId: Int): Boolean
}
