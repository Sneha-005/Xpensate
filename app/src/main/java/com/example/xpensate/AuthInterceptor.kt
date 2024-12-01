package com.example.xpensate

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
            Log.d("AuthInterceptor","$token")
        }
        return chain.proceed(request.build())
    }
}
