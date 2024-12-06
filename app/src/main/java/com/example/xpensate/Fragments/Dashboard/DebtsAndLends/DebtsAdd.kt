package com.example.xpensate.Fragments.Dashboard.DebtsAndLends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.app.DatePickerDialog
import java.util.Calendar
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
import android.app.TimePickerDialog
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.time.debounce

class DebtsAdd : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentDebtsAddBinding? = null
    private val binding get() = _binding!!
    private val buttonClickFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private var lastApiCallTime: Long = 0
    private val debounceApiDelay: Long = 500L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebtsAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        try {
            setupDatePicker()
            setupTimePicker()
            setupInputFilters()
            setupRadioButtons()
            lifecycleScope.launch {
                buttonClickFlow
                    .debounce(1000L)
                    .onEach { createDebtWithDebounce() }
                    .collect()
            }

            binding.addButton.setOnClickListener {
                buttonClickFlow.tryEmit(Unit)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTimePicker() {
        binding.timeField.setOnClickListener {
            try {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.timeField.setText(selectedTime)
                }, hour, minute, true)

                timePickerDialog.show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error selecting time", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createDebtWithDebounce() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastApiCallTime > debounceApiDelay) {
            lastApiCallTime = currentTime
            createDebt()
        } else {
            Toast.makeText(requireContext(), "Please wait...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDatePicker() {
        binding.dateField.setOnClickListener {
            try {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    binding.dateField.setText(selectedDate)
                }, year, month, day)

                datePickerDialog.show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error selecting date", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupInputFilters() {
        binding.amountField.filters = arrayOf(InputFilter { source, _, _, dest, _, _ ->
            val input = (dest.toString() + source.toString()).toDoubleOrNull()
            if (input != null) {
                when {
                    input > 10000000000 -> {
                        Toast.makeText(requireContext(), "Amount cannot exceed 10 billion", Toast.LENGTH_SHORT).show()
                        ""
                    }
                    input < 0 -> {
                        Toast.makeText(requireContext(), "Amount cannot be negative", Toast.LENGTH_SHORT).show()
                        ""
                    }
                    else -> null
                }
            } else null
        })
        binding.customTagsField.filters = arrayOf(InputFilter.LengthFilter(50))
        binding.customTagsField.setOnFocusChangeListener { _, hasFocus ->
            if (binding.customTagsField.text.length > 50) {
                Toast.makeText(requireContext(), "Tags cannot exceed 50 characters", Toast.LENGTH_SHORT).show()
            }
        }
        binding.categoryField.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.categoryField.setOnFocusChangeListener { _, hasFocus ->
            if (binding.categoryField.text.length > 20) {
                Toast.makeText(requireContext(), "Category name cannot exceed 20 characters", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupRadioButtons() {
        val debtButton: RadioButton = binding.debtButton
        val lendButton: RadioButton = binding.lendButton

        debtButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedRadio))
        debtButton.setOnClickListener {
            updateRadioButtons(debtButton, lendButton)
        }
        lendButton.setOnClickListener {
            updateRadioButtons(lendButton, debtButton)
            lendButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
        }
    }

    private fun updateRadioButtons(selected: RadioButton, unselected: RadioButton) {
        selected.isChecked = true
        unselected.isChecked = false
        selected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedRadio))
        unselected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.unselectedRadio))
        selected.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        unselected.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))
    }

    private fun createDebt() {
        try {
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

            AuthInstance.api.createDebt(name, note, amount, date, time, isLend).enqueue(object :
                Callback<CreateDebtResponse> {
                override fun onResponse(call: Call<CreateDebtResponse>, response: Response<CreateDebtResponse>) {
                    if (isAdded) {
                        try {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Debt created successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if(response.code() == 500){
                                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                                }
                                val errorBody = response.message().toString()
                                Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "An error occurred while handling the response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CreateDebtResponse>, t: Throwable) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
