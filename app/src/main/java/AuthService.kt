package com.example.xpensate.network

import com.example.xpensate.ForgetPassRequest
import com.example.xpensate.ForgetPassResponse
import com.example.xpensate.LoginRequest
import com.example.xpensate.LoginResponse
import com.example.xpensate.PassResetRequest
import com.example.xpensate.PassResetResponse
import com.example.xpensate.RegisterRequest
import com.example.xpensate.RegisterResponse
import com.example.xpensate.VerifyRequest
import com.example.xpensate.VerifyResetRequest
import com.example.xpensate.VerifyResetResponse
import com.example.xpensate.VerifyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login/")
    fun login(@Body loginAPI: LoginRequest): Call<LoginResponse>
    
    @POST("auth/register/")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("auth/verifyotp/")
    fun verify(@Body verifyRequest: VerifyRequest): Call<VerifyResponse>

    @POST("auth/passforget/")
    fun passforget(@Body forgetPassRequest: ForgetPassRequest): Call<ForgetPassResponse>

    @POST("auth/passreset/")
    fun passreset(@Body passResetRequest: PassResetRequest): Call<PassResetResponse>

    @POST("auth/pass/otpverify/")
    fun otpverify(@Body verifyResetRequest: VerifyResetRequest): Call<VerifyResetResponse>
}
