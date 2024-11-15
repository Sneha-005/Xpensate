package com.example.xpensate.API.auth.request

data class PassResetRequest(
    val email: String,
    val otp: String,
    val new_password: String
)