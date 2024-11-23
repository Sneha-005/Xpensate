package com.example.xpensate

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT

object TokenUtils {
    fun decodeToken(token: String): DecodedJWT {
        return JWT.decode(token)
    }

    fun getTokenPayload(token: String): Map<String, Any> {
        val decodedJWT = decodeToken(token)
        return decodedJWT.claims.mapValues { it.value.asString() }
    }

    fun getUsernameFromToken(token: String): String? {
        val decodedJWT = decodeToken(token)
        return decodedJWT.getClaim("username").asString()
    }
}