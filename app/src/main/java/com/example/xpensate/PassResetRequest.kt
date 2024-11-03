package com.example.xpensate

data class PassResetRequest(
    val confirm_password: String,
    val email: String,
    val new_password: String
)