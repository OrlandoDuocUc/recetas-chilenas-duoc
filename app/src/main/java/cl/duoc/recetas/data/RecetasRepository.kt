package cl.duoc.recetas.data

import cl.duoc.recetas.data.local.LocalStorage
import cl.duoc.recetas.data.remote.RecetasApiService
import cl.duoc.recetas.model.Ingredient
import cl.duoc.recetas.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecetasRepository(
    private val api: RecetasApiService,
    private val storage: LocalStorage
) {
    private fun mapRecipe(dto: cl.duoc.recetas.data.remote.RecipeDto): Recipe {
        val fav = storage.isFavorite(dto.id)
        return Recipe(
            id = dto.id,
            name = dto.name,
            region = dto.region,
            shortDescription = dto.shortDescription,
            description = dto.description,
            prepTimeMinutes = dto.prepTimeMinutes,
            difficulty = dto.difficulty,
            imageUrl = dto.imageUrl,
            isTraditional = dto.isTraditional,
            ingredients = dto.ingredients?.map {
                Ingredient(
                    name = it.name.orEmpty(),
                    quantity = it.quantity,
                    unit = it.unit,
                    order = it.order
                )
            } ?: emptyList(),
            isFavorite = fav
        )
    }

    suspend fun getRecetas(): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        runCatching { api.getRecetas().map { mapRecipe(it) } }
    }

    suspend fun getReceta(id: Int): Result<Recipe> = withContext(Dispatchers.IO) {
        runCatching { mapRecipe(api.getRecetaPorId(id)) }
    }

    fun toggleFavorite(id: Int, nowFavorite: Boolean) {
        storage.setFavorite(id, nowFavorite)
    }
}
