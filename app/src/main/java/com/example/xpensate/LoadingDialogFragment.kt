package com.example.xpensate

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.xpensate.R

class LoadingDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(): LoadingDialogFragment {
            return LoadingDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        return dialog
    }
}