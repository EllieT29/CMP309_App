package com.example.bloom.network

import com.google.gson.annotations.SerializedName

data class QuoteDC(
    //Specifying the key in the JSON response that corresponds to quote property
    @SerializedName("quote") val quote: String,
    //Specifying the key in the JSON response that corresponds to author property
    @SerializedName("author") val author: String
)