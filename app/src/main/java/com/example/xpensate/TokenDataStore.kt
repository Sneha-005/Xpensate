package com.example.xpensate

import android.content.Context
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AuthPreferences")

object TokenDataStore {
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val IMAGE_KEY = stringPreferencesKey("profile_image")

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64Str: String): Bitmap? {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    suspend fun saveImage(context: Context, bitmap: Bitmap) {
        val encodedImage = bitmapToBase64(bitmap)
        context.dataStore.edit { preferences ->
            preferences[IMAGE_KEY] = encodedImage
        }
    }

    fun getImage(context: Context): Flow<Bitmap?> {
        return context.dataStore.data.map { preferences ->
            val encodedImage = preferences[IMAGE_KEY]
            encodedImage?.let { base64ToBitmap(it) }
        }
    }

    suspend fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun clearTokens(context: Context) {
        context.dataStore.edit { preferences ->
            Log.d("clear","$preferences")
            preferences.clear()
        }
    }

    fun getAccessToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    fun getRefreshToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun saveUsername(context: Context, username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    fun getUsername(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }
    }

    suspend fun saveEmail(context: Context, email: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
        }
    }

    fun getEmail(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            val email = preferences[EMAIL_KEY]
            email
        }
    }

}