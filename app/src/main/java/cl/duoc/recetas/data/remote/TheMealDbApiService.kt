package cl.duoc.recetas.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// DTOs para TheMealDB
data class MealResponse(
    val meals: List<MealDto>?
)

data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?
)

interface TheMealDbApiService {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse
}

object TheMealDbApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val service: TheMealDbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TheMealDbApiService::class.java)
    }
}
