package cl.duoc.recetas.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecetasApiClient {
    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:73TCjzmQ/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val service: RecetasApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecetasApiService::class.java)
    }
}
