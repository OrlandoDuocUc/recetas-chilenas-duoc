package cl.duoc.recetas.ui.create

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.recetas.data.local.AppDatabase
import cl.duoc.recetas.data.local.SessionManager
import cl.duoc.recetas.data.local.entities.RecipeLocalEntity
import cl.duoc.recetas.model.Ingredient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class CreateRecipeUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class CreateRecipeViewModel(
    private val database: AppDatabase,
    private val sessionManager: SessionManager,
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(CreateRecipeUiState())
    val state: StateFlow<CreateRecipeUiState> = _state

    fun createRecipe(
        name: String,
        region: String?,
        shortDescription: String?,
        description: String,
        prepTimeMinutes: Int?,
        difficulty: String?,
        ingredients: List<Ingredient>,
        bitmap: Bitmap?,
        imageUri: Uri?
    ) {
        _state.value = CreateRecipeUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val userId = sessionManager.userId.firstOrNull()
                if (userId == null) {
                    _state.value = CreateRecipeUiState(error = "Usuario no autenticado")
                    return@launch
                }

                // Guardar imagen localmente si existe
                val imageUrl = when {
                    imageUri != null -> imageUri.toString()
                    bitmap != null -> saveBitmapAndGetPath(bitmap)
                    else -> null
                }

                val ingredientsJson = Gson().toJson(ingredients)

                val recipe = RecipeLocalEntity(
                    name = name,
                    region = region,
                    shortDescription = shortDescription,
                    description = description,
                    prepTimeMinutes = prepTimeMinutes,
                    difficulty = difficulty,
                    imageUrl = imageUrl,
                    ingredients = ingredientsJson,
                    userId = userId,
                    isSynced = false
                )

                database.recipeLocalDao().insert(recipe)
                _state.value = CreateRecipeUiState(success = true)

            } catch (e: Exception) {
                _state.value = CreateRecipeUiState(error = e.message ?: "Error al guardar receta")
            }
        }
    }

    private fun saveBitmapAndGetPath(bitmap: Bitmap): String {
        // Por simplicidad, retornamos un placeholder
        // En producción guardarías el bitmap en almacenamiento interno
        return "local_image_${System.currentTimeMillis()}"
    }
}

class CreateRecipeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateRecipeViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val sessionManager = SessionManager(application)
            @Suppress("UNCHECKED_CAST")
            return CreateRecipeViewModel(database, sessionManager, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
