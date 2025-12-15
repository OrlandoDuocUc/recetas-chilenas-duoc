package cl.duoc.recetas.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.recetas.data.RecetasRepository

class RecipeDetailViewModelFactory(
    private val repository: RecetasRepository,
    private val recipeId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeDetailViewModel(repository, recipeId) as T
    }
}
