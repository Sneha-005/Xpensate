package com.example.xpensate

data class VerifyRequest(
    val email: String,
    val otp: String
)