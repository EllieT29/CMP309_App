package com.example.bloom.network

import retrofit2.http.GET

interface QuoteApiService {
    @GET("quoteoftheday?category=happiness")//Getting daily quote
    suspend fun getQuotes(): List<QuoteDC>
}