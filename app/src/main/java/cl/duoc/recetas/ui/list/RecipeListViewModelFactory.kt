package cl.duoc.recetas.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.recetas.data.RecetasRepository

class RecipeListViewModelFactory(
    private val repository: RecetasRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeListViewModel(repository) as T
    }
}
