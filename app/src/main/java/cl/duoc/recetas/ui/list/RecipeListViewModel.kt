package cl.duoc.recetas.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.recetas.data.RecetasRepository
import cl.duoc.recetas.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val data: List<Recipe> = emptyList(),
    val error: String? = null
)

class RecipeListViewModel(
    private val repository: RecetasRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeListUiState(isLoading = true))
    val state: StateFlow<RecipeListUiState> = _state

    init { loadRecetas() }

    fun loadRecetas() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.getRecetas()
            result.onSuccess { list ->
                _state.value = RecipeListUiState(isLoading = false, data = list, error = null)
            }.onFailure { e ->
                _state.value = RecipeListUiState(isLoading = false, data = emptyList(), error = e.message)
            }
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        val nowFavorite = !recipe.isFavorite
        repository.toggleFavorite(recipe.id, nowFavorite)
        val updated = _state.value.data.map {
            if (it.id == recipe.id) it.copy(isFavorite = nowFavorite) else it
        }
        _state.value = _state.value.copy(data = updated)
    }
}
