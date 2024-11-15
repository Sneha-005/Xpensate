package com.example.xpensate.API.auth.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirm_password: String

)