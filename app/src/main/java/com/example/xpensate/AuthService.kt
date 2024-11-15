
package com.example.xpensate.network

import com.example.xpensate.API.auth.request.ForgetPassRequest
import com.example.xpensate.API.auth.response.ForgetPassResponse
import com.example.xpensate.API.auth.request.LoginRequest
import com.example.xpensate.API.auth.response.LoginResponse
import com.example.xpensate.API.auth.request.PassResetRequest
import com.example.xpensate.API.auth.response.PassResetResponse
import com.example.xpensate.API.auth.request.RegisterRequest
import com.example.xpensate.API.auth.response.RegisterResponse
import com.example.xpensate.API.auth.request.VerifyRequest
import com.example.xpensate.API.auth.request.VerifyResetRequest
import com.example.xpensate.API.auth.response.VerifyResetResponse
import com.example.xpensate.API.auth.response.VerifyResponse
import com.example.xpensate.API.home.ExpensesByDay
import com.example.xpensate.API.home.CategoryChartResponse
import com.example.xpensate.API.home.lineGraph
import com.example.xpensate.RecordsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

    @GET("expense/days-expense/")
    fun getLineGraphData():Call<lineGraph>

    @GET("expense/list/")
    fun expenselist():Call<RecordsResponse>

    @GET("expense/category-expense/")
    fun expenseChart():Call<CategoryChartResponse>
}
