package cl.duoc.recetas.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val role: String, // "admin" o "user"
    val createdAt: Long = System.currentTimeMillis()
)
