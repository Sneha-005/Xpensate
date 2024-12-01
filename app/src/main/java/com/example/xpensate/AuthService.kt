package com.example.xpensate.network

import com.example.xpensate.API.BudgetBuilder.BudgetExpensesResponse
import com.example.xpensate.API.BudgetBuilder.CreateBudgetResponse
import com.example.xpensate.API.BudgetBuilder.CreateMonthlyLimitResponse
import com.example.xpensate.API.TripTracker.AddTripExpense
import com.example.xpensate.API.TripTracker.AddTripMemberResponse
import com.example.xpensate.API.TripTracker.CreateGroupResponse.CreateGroupResponse
import com.example.xpensate.API.TripTracker.CreateTripResponse
import com.example.xpensate.API.TripTracker.JoinGroupResponse
import com.example.xpensate.API.TripTracker.RemoveDataResponse
import com.example.xpensate.API.TripTracker.UserGroupDetailsResponse
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
import com.example.xpensate.API.fcmTokenResponse
import com.example.xpensate.API.home.AddExpenses
import com.example.xpensate.API.home.AppCurrency
import com.example.xpensate.API.home.CategoryChartResponse
import com.example.xpensate.API.home.CurrencyConverter.CurrencyConvertAPI
import com.example.xpensate.API.home.CurrencyConverter.CurrencyData
import com.example.xpensate.API.home.DebtsAndLends.Debtmark
import com.example.xpensate.API.home.DebtsAndLends.DebtsData
import com.example.xpensate.API.home.UpdateContact.OtpVerifyRequest
import com.example.xpensate.API.home.RecentSplitBillsResponse
import com.example.xpensate.API.home.SplitBillFeature.AddMember
import com.example.xpensate.API.home.SplitBillFeature.CreateBillRequest
import com.example.xpensate.API.home.SplitBillFeature.CreateBillResponse
import com.example.xpensate.API.home.SplitBillFeature.CreateGroup
import com.example.xpensate.API.home.SplitBillFeature.DeleteMember
import com.example.xpensate.API.home.SplitBillFeature.GroupMembers.GroupMembers
import com.example.xpensate.API.home.SplitBillFeature.Groups.GroupsResponse
import com.example.xpensate.API.home.SplitBillFeature.MarkPaidResponse
import com.example.xpensate.API.home.SplitBillFeature.SplitGroupShowDetails.SplitGroupDetails
import com.example.xpensate.API.home.UpdateContact.UpdateContactOtpVerify
import com.example.xpensate.API.home.UpdateContact.UpdateContactResponse
import com.example.xpensate.API.home.UpdateUsernameResponse
import com.example.xpensate.API.home.lineGraph
import com.example.xpensate.API.home.CategoryList.CategoriesList
import com.example.xpensate.API.home.UpdateContact.ProfileImage
import com.example.xpensate.Modals.CreateDebtResponse
import com.example.xpensate.Modals.RecordsResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    fun updateName(@Field("name") name: String):Call<UpdateUsernameResponse>

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

    @FormUrlEncoded
    @POST("triptrack/createdebt/")
    fun createDebt(
        @Field("name") name: String,
        @Field("note") note: String,
        @Field("amount") amount: Double,
        @Field("date") date: String,
        @Field("time") time: String,
        @Field("lend") lend: Boolean
    ): Call<CreateDebtResponse>

    @GET("triptrack/debts/")
    fun getDebts(): Call<DebtsData>

    @FormUrlEncoded
    @POST("triptrack/markdebtpaid/")
    fun markDebtPaid(@Field("debt_id") debtId: Int): Call<Debtmark>

    @FormUrlEncoded
    @POST("split/add/members/")
    fun addMemberToGroup(
        @Field("group") group: String,
        @Field("member") member: String
    ): Call<AddMember>

    @GET("split/groups/")
    fun getGroups(): Call<GroupsResponse>

    @FormUrlEncoded
    @POST("split/groupmembers/")
    fun getGroupMembers(
        @Field("group") groupId: String
    ): Call<GroupMembers>

    @FormUrlEncoded
    @POST("split/addgroup/")
    fun addGroup(
        @Field("name") name: String,
        @Field("member") member: String
    ): Call<CreateGroup>

    @DELETE("split/remove/members/{groupId}/{member}/")
    fun removeMember(
        @Path("groupId") groupId: String,
        @Path("member") member: String
    ): Call<DeleteMember>

    @POST("split/createbill/")
    fun createBill(
        @Body createBillRequest: CreateBillRequest
    ): Call<CreateBillResponse>

    @POST("split/markpaid/{groupId}/")
    fun markBillAsPaid(
        @Path("groupId") groupId: String,
        @Query("member") memberEmail: String
    ): Call<MarkPaidResponse>

    @GET("split/groupdetail/{groupId}/")
    fun showSplitDetail(
        @Path("groupId") groupId: String
    ): Call<SplitGroupDetails>

    @FormUrlEncoded
    @POST("triptrack/create/")
    fun createTrip(
        @Field("name") name: String
    ): Call<CreateTripResponse>

    @FormUrlEncoded
    @POST("triptrack/joingroup/")
    fun joinGroup(
        @Field("invitecode") invitecode: String
    ): Call<JoinGroupResponse>

    @GET("triptrack/usergroups/")
    fun userGrpDetails(): Call<UserGroupDetailsResponse>

    @GET("triptrack/tripgroup/{id}/")
    fun getTripGroupDetails(
        @Path("id") groupId: String
    ): Call<CreateGroupResponse>

    @DELETE("triptrack/add-remove/{id}/{email}/")
    fun removeTripMember(
        @Path("id") id: String,
        @Path("email") email: String
    ): Call<RemoveDataResponse>

    @FormUrlEncoded
    @POST("triptrack/addexp/{tripId}/")
    fun addExpense(
        @Path("tripId") tripId: Int,
        @Field("amount") amount: Double,
        @Field("paidby") paidBy: String,
        @Field("whatfor") whatFor: String
    ): Call<AddTripExpense>

    @FormUrlEncoded
    @POST("triptrack/add-remove/{id}/{email}/")
    fun addTripMember(
        @Path("id") id: String,
        @Path("email") email: String
    ): Call<AddTripMemberResponse>

    @FormUrlEncoded
    @POST("expense/create-budget/")
    fun createBudgetLimit(
        @Field("need") need: Double,
        @Field("luxury") luxury: Double,
        @Field("savings") savings: Double
    ): Call<CreateBudgetResponse>

    @POST("expense/monthly-limit/")
    fun setMonthlyLimit(
        @Field("monthlylimit") monthlylimit: Double
    ): Call<CreateMonthlyLimitResponse>

    @GET("expense/budgetexpenses/")
    fun getBudgetExpenses(): Call<BudgetExpensesResponse>

    @GET("expense/category/")
    fun getCategoryList(): Call<CategoriesList>

    @Multipart
    @POST("upload/profile-image")
    fun uploadProfileImage(@Part image: MultipartBody.Part): Call<ProfileImage>

    @FormUrlEncoded

    @POST("auth/recive/devicetoken/")
    fun fcmToken(
        @Field("fcm_token") fcmToken: String
    ): Call<fcmTokenResponse>
}
