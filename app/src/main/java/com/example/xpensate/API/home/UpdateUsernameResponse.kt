package com.example.xpensate.API.home

import com.example.xpensate.API.auth.tokens.Tokens
import android.media.session.MediaSession.Token

data class UpdateUsernameResponse(
    val message: String,
    val success: String,
    val tokens: Tokens
)