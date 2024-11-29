package com.example.xpensate

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.util.Log
import com.example.xpensate.network.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

object AuthInstance {

    private const val BASE_URL = "http://13.201.141.215/"
    private var retrofit: Retrofit? = null
    private var authenticated = false

    fun init(context: Context) {
        authenticated = checkAuthentication(context)
        val token = runBlocking {
            TokenDataStore.getAccessToken(context).first()
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val gson = GsonBuilder().setLenient().create()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun isAuthenticated(): Boolean {
        return authenticated
    }

    private fun checkAuthentication(context: Context): Boolean {
        // Implement your authentication check logic here
        return true
    }

    val api: ApiService by lazy {
        retrofit?.create(ApiService::class.java) ?: throw IllegalStateException("Retrofit instance is not initialized")
    }
}