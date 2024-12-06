package com.example.xpensate

import android.app.ProgressDialog
import android.content.Context

object ProgressDialogHelper {
    private var progressDialog: ProgressDialog? = null
    private var lastCallTime: Long = 0
    private const val THROTTLE_TIME_MS = 2000

    fun showProgressDialog(context: Context) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastCallTime >= THROTTLE_TIME_MS) {
            if (progressDialog == null) {
                progressDialog = ProgressDialog(context).apply {
                    setMessage("Loading...")
                    setCancelable(false)
                    show()
                }
            }
            lastCallTime = currentTime
        }
    }

    fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }
}
