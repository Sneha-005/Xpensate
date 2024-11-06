package com.example.xpensate

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirm_password: String

)