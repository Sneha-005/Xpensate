package com.example.xpensate

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

// This is the Interceptor class that adds the Authorization header to all requests
class AuthInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Check if the token is null or empty before adding it
        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            // Add the Authorization header
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

