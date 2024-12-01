package com.example.xpensate.Fragments.Dashboard.DebtsAndLends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.xpensate.AuthInstance
import com.example.xpensate.Modals.CreateDebtResponse
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentDebtsAddBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DebtsAdd : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentDebtsAddBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDebtsAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val debtButton: RadioButton = binding.debtButton
        val lendButton: RadioButton = binding.lendButton

        debtButton.setOnClickListener {
            binding.debtButton.isChecked = true
            binding.lendButton.isChecked = false
            debtButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(),
                    R.color.selectedRadio
            ))
            lendButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(),
                    R.color.unselectedRadio
            ))
            debtButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            lendButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))
        }

        lendButton.setOnClickListener {
            lendButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(),
                    R.color.selectedRadio
            ))
            debtButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(),
                    R.color.unselectedRadio
            ))
            lendButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            debtButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))
        }

        binding.addButton.setOnClickListener {
            createDebt()
        }
    }
    private fun createDebt() {
        val amountString = binding.amountField.text.toString().trim()
        val note = binding.customTagsField.text.toString().trim()
        val date = binding.dateField.text.toString().trim()
        val time = binding.timeField.text.toString().trim()
        val name = binding.categoryField.text.toString().trim()
        val isLend = binding.lendButton.isChecked

        val amount = amountString.toDoubleOrNull()

        if (amount == null || name.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill amount and name fields", Toast.LENGTH_SHORT).show()
            return
        }
        AuthInstance.api.createDebt(name,note, amount, date, time, isLend).enqueue(object :
            Callback<CreateDebtResponse> {
            override fun onResponse(call: Call<CreateDebtResponse>, response: Response<CreateDebtResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Debt created successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to create debt: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateDebtResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}