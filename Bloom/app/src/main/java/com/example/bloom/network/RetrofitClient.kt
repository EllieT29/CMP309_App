package com.example.bloom.network

import com.example.bloom.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Used a mix of Practical example and Google Gemini for HTTP communication with 3rd-party API
object RetrofitClient {
    private const val BASE_URL = "https://api.api-ninjas.com/v2/"
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val newRequest = originalRequest.newBuilder()
                .header("accept", "application/json")
                .header("X-Api-Key", BuildConfig.API_KEY)
                .build()

            chain.proceed(newRequest)
        }
        .build()

    val instance: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(QuoteApiService::class.java)
    }
}
