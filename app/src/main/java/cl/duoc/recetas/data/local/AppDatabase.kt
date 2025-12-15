package cl.duoc.recetas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.recetas.data.local.dao.FavoriteDao
import cl.duoc.recetas.data.local.dao.RecipeLocalDao
import cl.duoc.recetas.data.local.dao.UserDao
import cl.duoc.recetas.data.local.entities.FavoriteEntity
import cl.duoc.recetas.data.local.entities.RecipeLocalEntity
import cl.duoc.recetas.data.local.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, RecipeLocalEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun recipeLocalDao(): RecipeLocalDao
    abstract fun favoriteDao(): FavoriteDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recetas_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Crear usuario admin por defecto
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                database.userDao().insert(
                                    UserEntity(
                                        email = "admin@recetas.cl",
                                        password = "admin123",
                                        name = "Administrador",
                                        role = "admin"
                                    )
                                )
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
