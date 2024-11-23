package com.example.xpensate.API.home

data class OtpVerifyRequest(
    val contact: String,
    val otp: String
)

