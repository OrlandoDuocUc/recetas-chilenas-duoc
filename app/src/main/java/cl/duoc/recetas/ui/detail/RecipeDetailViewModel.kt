package cl.duoc.recetas.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.recetas.data.RecetasRepository
import cl.duoc.recetas.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val data: Recipe? = null,
    val error: String? = null
)

class RecipeDetailViewModel(
    private val repository: RecetasRepository,
    private val recipeId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailUiState(isLoading = true))
    val state: StateFlow<RecipeDetailUiState> = _state

    init { load() }

    fun load() {
        _state.value = RecipeDetailUiState(isLoading = true)
        viewModelScope.launch {
            val result = repository.getReceta(recipeId)
            result.onSuccess { recipe ->
                _state.value = RecipeDetailUiState(isLoading = false, data = recipe)
            }.onFailure { e ->
                _state.value = RecipeDetailUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun toggleFavorite() {
        val current = _state.value.data ?: return
        val nowFav = !current.isFavorite
        repository.toggleFavorite(current.id, nowFav)
        _state.value = _state.value.copy(data = current.copy(isFavorite = nowFav))
    }
}
