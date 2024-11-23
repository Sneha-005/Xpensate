
package com.example.xpensate.network

import android.text.Editable
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
import com.example.xpensate.API.home.AddExpenses
import com.example.xpensate.API.home.AppCurrency
import com.example.xpensate.API.home.CategoryChartResponse
import com.example.xpensate.API.home.CurrencyConvertAPI
import com.example.xpensate.API.home.CurrencyData
import com.example.xpensate.API.home.OtpVerifyRequest
import com.example.xpensate.API.home.RecentSplitBillsResponse
import com.example.xpensate.API.home.UpdateContactOtpVerify
import com.example.xpensate.API.home.UpdateContactResponse
import com.example.xpensate.API.home.UpdateUsernameResponse
import com.example.xpensate.API.home.lineGraph
import com.example.xpensate.Modals.RecordsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
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

    @GET("analytics/convert-currency/")
    fun currencyData():Call<CurrencyData>

    @FormUrlEncoded
    @POST("analytics/convert-currency/")
    fun currencyConvert(
        @Field("from_currency") fromCurrency: String,
        @Field("to_currency") toCurrency: String,
        @Field("money") amount: String
    ): Call<CurrencyConvertAPI>

    @FormUrlEncoded
    @POST("auth/username/")
    fun updateName(@Field("name") name: Editable):Call<UpdateUsernameResponse>

    @FormUrlEncoded
    @POST("auth/phoneverify/")
    fun updateContact(@Field ("contact") contact: String): Call<UpdateContactResponse>

    @POST("auth/phone/otpverify/")
    fun updateVerify(@Body request: OtpVerifyRequest): Call<UpdateContactOtpVerify>

    @FormUrlEncoded
    @POST("expense/create/")
    fun addExpense(
        @Field("amount") amount: String,
        @Field("note") note: String,
        @Field("date") date: String,
        @Field("time") time: String,
        @Field("category") category: String,
        @Field("image") image: String?,
        @Field("is_credit") isCredit: Boolean
    ): Call<AddExpenses>

    @GET("split/recentsplits/")
    fun getSplitGroups(): Call<RecentSplitBillsResponse>

    @FormUrlEncoded
    @POST("auth/addcurrency/")
    fun appCurrency(@Field("currency") currency: String): Call<AppCurrency>
}
