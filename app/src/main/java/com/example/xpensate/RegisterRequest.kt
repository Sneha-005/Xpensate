package com.example.xpensate

data class RegisterRequest(
    val confirm_password: String,
    val email: String,
    val password: String
)