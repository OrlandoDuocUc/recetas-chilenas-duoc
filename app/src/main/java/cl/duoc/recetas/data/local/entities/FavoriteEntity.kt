package cl.duoc.recetas.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recipeId: Int,
    val userId: Int,
    val createdAt: Long = System.currentTimeMillis()
)
