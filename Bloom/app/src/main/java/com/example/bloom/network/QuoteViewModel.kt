package com.example.bloom.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


//Used a mix of Practical example and Google Gemini for HTTP communication with 3rd-party API
class QuoteViewModel : ViewModel() {
    var quotes by mutableStateOf<List<QuoteDC>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    //Initialising the API service
    private val apiService = RetrofitClient.instance

    //Fetching the quotes and handling errors
    fun fetchQuotes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = apiService.getQuotes()
                quotes = result
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}