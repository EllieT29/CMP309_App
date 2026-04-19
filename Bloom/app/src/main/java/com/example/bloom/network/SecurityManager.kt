package com.example.bloom.network

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import java.security.GeneralSecurityException

//Used https://medium.com/pickme-engineering-blog/securing-api-keys-and-sensitive-data-in-android-apps-a-practical-modern-guide-ab735ca11502
//Used Google gemini to fix bug and handle exceptions better
class ApiSecurityManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val fileName = "secure_prefs"

    private val encryptedprefs: SharedPreferences = try {
        createSharedPrefs(context)
    } catch (e: Exception) {
        context.deleteSharedPreferences(fileName)
        createSharedPrefs(context)
    }

    private fun createSharedPrefs(context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveApiKey(apiKey: String) {
        encryptedprefs.edit { putString("api_key", apiKey) }
    }

    fun getApiKey(): String? {
        return try {
            encryptedprefs.getString("api_key", null)
        } catch (e: Exception) {
            // Handle potential decryption errors when reading
            null
        }
    }
}