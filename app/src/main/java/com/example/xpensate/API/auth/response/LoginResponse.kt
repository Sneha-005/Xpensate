package com.example.xpensate.API.auth.response

import com.example.xpensate.API.auth.tokens.Tokens

data class LoginResponse(
    val message: String,
    val tokens: Tokens
)