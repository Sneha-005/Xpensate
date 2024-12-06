package com.example.xpensate.Activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.xpensate.databinding.ActivityMainBinding
import android.util.Log
import androidx.core.content.ContextCompat
import android.os.Build
import com.example.xpensate.API.fcmTokenResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import android.content.pm.PackageManager
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var dialog: Dialog? = null
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this@MainActivity,
                "Post notification permission granted!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Post notification permission not granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AuthInstance.init(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (AuthInstance.isAuthenticated() || destination.id == R.id.blankFragment) {
                navigateToHome()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(object : OnCompleteListener<String?> {
            override fun onComplete(task: com.google.android.gms.tasks.Task<String?>) {
                if (!task.isSuccessful) {
                    Toast.makeText(this@MainActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                    return
                }
                val token: String? = task.result
                if (token != null) {
                    Log.d("Token: ", token)
                    sendTokenToServer(token)
                }
            }
        })
    }

    private fun sendTokenToServer(token: String) {
        AuthInstance.api.fcmToken(token).enqueue(object : Callback<fcmTokenResponse> {
            override fun onResponse(call: Call<fcmTokenResponse>, response: Response<fcmTokenResponse>) {
                if (response.isSuccessful) {
                    Log.d("token","$token")
                    Log.d("FCM Token", "Token sent successfully")
                } else {
                    Log.d("FCM Token", "Failed to send token")
                }
            }

            override fun onFailure(call: Call<fcmTokenResponse>, t: Throwable) {
                Log.d("FCM Token", "Error: ${t.message}")
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return NavigationUI.navigateUp(navController, null) || super.onSupportNavigateUp()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}