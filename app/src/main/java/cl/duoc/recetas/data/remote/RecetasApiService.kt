package cl.duoc.recetas.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class IngredientDto(
    val name: String?,
    val quantity: Double?,
    val unit: String?,
    val order: Int?
)

data class RecipeDto(
    val id: Int,
    val name: String,
    val region: String?,
    @SerializedName("short_description") val shortDescription: String?,
    val description: String,
    @SerializedName("prep_time_minutes") val prepTimeMinutes: Int?,
    val difficulty: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_traditional") val isTraditional: Boolean?,
    val ingredients: List<IngredientDto>?
)

interface RecetasApiService {
    @GET("recetas")
    suspend fun getRecetas(): List<RecipeDto>

    @GET("recetas/{recetas_id}")
    suspend fun getRecetaPorId(@Path("recetas_id") id: Int): RecipeDto

    @POST("recetas")
    suspend fun crearReceta(@Body receta: RecipeDto): RecipeDto

    @PATCH("recetas/{recetas_id}")
    suspend fun actualizarReceta(
        @Path("recetas_id") id: Int,
        @Body campos: Map<String, @JvmSuppressWildcards Any?>
    ): RecipeDto
}
