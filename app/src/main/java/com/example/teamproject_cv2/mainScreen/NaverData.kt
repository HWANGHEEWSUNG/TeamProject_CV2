package com.example.teamproject_cv2.mainScreen

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class NewsItem(
    val title: String,
    val description: String
)

data class NewsResponse(
    val items: List<NewsItem>
)
object RetrofitInstance {
    val api: NaverApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NaverApiService::class.java)
    }
}

interface NaverApiService {
    @GET("v1/search/news.json")
    suspend fun getNews(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int
    ): NewsResponse
}
