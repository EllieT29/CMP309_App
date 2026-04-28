package com.example.bloom.network

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

//Used https://medium.com/pickme-engineering-blog/securing-api-keys-and-sensitive-data-in-android-apps-a-practical-modern-guide-ab735ca11502
//Used Google gemini to fix bug and handle exceptions better

//Define class to manage API security
class ApiSecurityManager(context: Context) {
    //MasterKey is used for encrypting and decrypting data
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val fileName = "secure_prefs"

    //Encrypted SharedPreferences instance to securely store sensitive data
    private val encryptedprefs: SharedPreferences = try {
        createSharedPrefs(context)
    } catch (e: Exception) {//If an error occurs delete the old preferences and recreate them
        context.deleteSharedPreferences(fileName)
        createSharedPrefs(context)
    }

    //Create and return an instance of EncryptedSharedPreferences
    private fun createSharedPrefs(context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    //Save the API key to the encrypted SharedPreferences
    fun saveApiKey(apiKey: String) {
        encryptedprefs.edit { putString("api_key", apiKey) }
    }

    //Retrieve the API key from the encrypted SharedPreferences
    fun getApiKey(): String? {
        return try {
            encryptedprefs.getString("api_key", null)
        } catch (e: Exception) {
            // Handle potential decryption errors when reading
            null
        }
    }
}