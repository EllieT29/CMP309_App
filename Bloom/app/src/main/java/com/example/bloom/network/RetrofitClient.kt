package com.example.bloom.network

import android.content.Context
import com.example.bloom.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Used a mix of Practical example and Google Gemini for HTTP communication with 3rd-party API
object RetrofitClient {
    //Base URL for the API
    private const val BASE_URL = "https://api.api-ninjas.com/v2/"

    //Used Google Gemini for getting Api key
    var securityManager: ApiSecurityManager? = null

    //Initialising the security manager
    fun init(context: Context) {
        securityManager = ApiSecurityManager(context)
    }

    //Define the OkHttpClient with an interceptor for custom headers
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            //Get original request
            val originalRequest = chain.request()

            //Retrieve API key securely or use the default
            val apiKey = securityManager?.getApiKey() ?: BuildConfig.API_KEY

            // Build a new request with custom headers
            val newRequest = originalRequest.newBuilder()
                .header("accept", "application/json")
                .header("X-Api-Key", apiKey)
                .build()

            //Proceed with new request
            chain.proceed(newRequest)
        }
        .build()// Build the OkHttpClient instance with the interceptor added

    // Create a singleton instance of the Retrofit service for QuoteApiService
    val instance: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)//Set base URL for API
            .addConverterFactory(GsonConverterFactory.create())//Parse JSON into Kotlin objects using Gson.
            .client(okHttpClient)
            .build()
            .create(QuoteApiService::class.java)// Create an instance of QuoteApiService for API calls
    }
}
