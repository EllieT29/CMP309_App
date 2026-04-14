package com.example.bloom.network

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

//Used https://medium.com/pickme-engineering-blog/securing-api-keys-and-sensitive-data-in-android-apps-a-practical-modern-guide-ab735ca11502
class ApiSecurityManager(context: Context) {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val encryptedprefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    fun saveApiKey(apiKey: String) {
        encryptedprefs.edit { putString("api_key", apiKey) }
    }

    fun getApiKey(): String? {
        return encryptedprefs.getString("api_key", null)
    }
}