package com.example.bloom.network

import retrofit2.http.GET

interface QuoteApiService {
    //Fetches a list of quotes from the API
    @GET("quoteoftheday?category=happiness")
    suspend fun getQuotes(): List<QuoteDC>
}
