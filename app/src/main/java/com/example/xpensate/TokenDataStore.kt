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
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AuthPreferences")

object TokenDataStore {
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val IMAGE_KEY = stringPreferencesKey("profile_image")
    private val _imageFlow = MutableStateFlow<Bitmap?>(null)
    private val CURRENCY_RATE_KEY = stringPreferencesKey("currency_rate")
    private val PREFERRED_CURRENCY_KEY = stringPreferencesKey("preferred_currency")
    val imageFlow: StateFlow<Bitmap?> = _imageFlow
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
        _imageFlow.update{bitmap}
    }

    fun getImage(context: Context): Flow<Bitmap?> {
        return context.dataStore.data.map { preferences ->
            val encodedImage = preferences[IMAGE_KEY]
            encodedImage?.let { base64ToBitmap(it) }
        }.also { flow ->
            runBlocking {
                _imageFlow.update { flow.first() }
            }
        }
    }
    suspend fun loadImageOnStart(context: Context) {
        val image = getImage(context).first()
        _imageFlow.update { image }
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

    suspend fun saveCurrencyRate(context: Context, rate: Double){
        context.dataStore.edit { preferences ->
            preferences[doublePreferencesKey("currency_rate")] = rate
        }
    }
    fun getCurrencyRate(context: Context): Flow<Double?> {
        Log.d("currencyToken","${CURRENCY_RATE_KEY}")
        return context.dataStore.data.map { preferences ->
            preferences[doublePreferencesKey("currency_rate")] ?: 1.0
        }
    }



    fun getPreferredCurrency(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PREFERRED_CURRENCY_KEY]
        }
    }

    suspend fun savePreferredCurrency(context: Context, currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PREFERRED_CURRENCY_KEY] = currency
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