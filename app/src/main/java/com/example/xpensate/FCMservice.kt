package com.example.xpensate

import com.example.xpensate.API.fcmTokenResponse
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FCMservice  {
/*    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToBackend(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        println("FCM Message: ${remoteMessage.data}")
    }
    fun sendTokenToBackend(fcmToken: String?) {
        if (fcmToken.isNullOrEmpty()) return


        val tokenRequest = mapOf("fcm_token" to fcmToken)

        AuthInstance.api.fcmToken(tokenRequest.toString()).enqueue(object : Callback<fcmTokenResponse> {
            override fun onResponse(call: Call<fcmTokenResponse>, response: Response<fcmTokenResponse>) {
                if (response.isSuccessful) {
                    println("FCM token sent successfully.")
                } else {
                    println("Failed to send FCM token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<fcmTokenResponse>, t: Throwable) {
                println("Error sending FCM token: ${t.localizedMessage}")
            }
        })
    }*/
}
