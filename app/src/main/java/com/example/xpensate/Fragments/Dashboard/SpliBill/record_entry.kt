package com.example.xpensate.Fragments.Dashboard.SpliBill

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.xpensate.API.home.AddExpenses
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentRecordEntryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordEntry : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentRecordEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val incomeButton: RadioButton = binding.incomeButton
        val expenseButton: RadioButton = binding.expenseButton

        incomeButton.setOnClickListener {
            binding.incomeButton.isChecked = true
            binding.expenseButton.isChecked = false
            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.selectedRadio
            ))
            expenseButton.setBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.unselectedRadio
            ))
            incomeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            expenseButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))

        }

        expenseButton.setOnClickListener {
            expenseButton.setBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.selectedRadio
            ))
            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.unselectedRadio
            ))
            expenseButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            incomeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))
        }

        binding.addButton.setOnClickListener {
            addExpenseToApi()
        }

    }

    private fun addExpenseToApi() {
        val amount = binding.amountField.text.toString().trim()
        val note = binding.customTagsField.text.toString().trim()
        val date = binding.dateField.text.toString().trim()
        val time = binding.timeField.text.toString().trim()
        val category = binding.categoryField.text.toString().trim()
        val image = null
        val isCredit = binding.incomeButton.isChecked

        if (amount.isEmpty()|| category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        AuthInstance.api.addExpense(amount, note, date, time, category, image, isCredit)
            .enqueue(object : Callback<AddExpenses> {
                override fun onResponse(call: Call<AddExpenses>, response: Response<AddExpenses>) {
                    Log.d("Add","ResponseCode: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        if (result.success == "true") {
                            Toast.makeText(requireContext(), "Expense added successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to add expense", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AddExpenses>, t: Throwable) {
                    Toast.makeText(requireContext(), "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}