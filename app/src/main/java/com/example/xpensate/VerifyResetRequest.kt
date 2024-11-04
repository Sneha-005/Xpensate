package com.example.xpensate

data class VerifyResetRequest(
    val email: String,
    val otp: String
)