package com.example.xpensate.API.auth.response

import com.example.xpensate.API.auth.tokens.TokensVerify

data class VerifyResponse(
    val message: String,
    val tokens: TokensVerify
)