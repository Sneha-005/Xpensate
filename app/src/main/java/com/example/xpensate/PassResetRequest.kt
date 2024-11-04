package com.example.xpensate

data class PassResetRequest(
    val email: String,
    val otp: String,
    val new_password: String
)