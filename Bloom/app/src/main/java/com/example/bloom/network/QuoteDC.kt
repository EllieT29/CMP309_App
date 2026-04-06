package com.example.bloom.network

import com.google.gson.annotations.SerializedName

data class QuoteDC(
    @SerializedName("quote") val quote: String,
    @SerializedName("author") val author: String
)