package com.example.xpensate.API.auth.request

data class VerifyResetRequest(
    val email: String,
    val otp: String
)