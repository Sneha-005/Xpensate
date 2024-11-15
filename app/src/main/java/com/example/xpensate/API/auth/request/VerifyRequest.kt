package com.example.xpensate.API.auth.request

data class VerifyRequest(
    val email: String,
    val otp: String
)