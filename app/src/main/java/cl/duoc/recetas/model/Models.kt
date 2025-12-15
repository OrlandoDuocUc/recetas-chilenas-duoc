package cl.duoc.recetas.model

data class Ingredient(
    val name: String = "",
    val quantity: Double? = null,
    val unit: String? = null,
    val order: Int? = null
)

data class Recipe(
    val id: Int = 0,
    val name: String = "",
    val region: String? = null,
    val shortDescription: String? = null,
    val description: String = "",
    val prepTimeMinutes: Int? = null,
    val difficulty: String? = null,
    val imageUrl: String? = null,
    val isTraditional: Boolean? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val isFavorite: Boolean = false
)
