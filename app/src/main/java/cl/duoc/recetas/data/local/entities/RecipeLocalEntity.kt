package cl.duoc.recetas.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes_local")
data class RecipeLocalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val region: String?,
    val shortDescription: String?,
    val description: String,
    val prepTimeMinutes: Int?,
    val difficulty: String?,
    val imageUrl: String?,
    val ingredients: String, // JSON string
    val userId: Int, // Quién creó esta receta
    val isSynced: Boolean = false, // Si ya se subió al servidor
    val createdAt: Long = System.currentTimeMillis()
)
