package com.example.xpensate.Fragments.Dashboard.SpliBill

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.AddExpenses
import com.example.xpensate.API.home.CategoryList.CategoriesListItem
import com.example.xpensate.Adapters.CategoriesListAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentRecordEntryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class RecordEntry : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentRecordEntryBinding? = null
    private val binding get() = _binding!!
    private val imageMap = mapOf(
        "Food" to R.drawable.food_icon,
        "Housing" to R.drawable.housing_icon,
        "Shopping" to R.drawable.shopping_icon,
        "Investment" to R.drawable.investment_icon,
        "Life and Entertainment" to R.drawable.life_icon,
        "Technical Appliance" to R.drawable.tech_icon,
        "Transporation" to R.drawable.transportation_icon
    )

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
        setupDatePicker()
        setupTimePicker()
        setupInputFilters()
        val incomeButton: RadioButton = binding.incomeButton
        val expenseButton: RadioButton = binding.expenseButton

        binding.categoryList.isVisible = false
        fetchCategories()
        binding.categoryField.setOnClickListener {
            binding.categoryList.isVisible = !binding.categoryList.isVisible
        }
        incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedRadio))

        incomeButton.setOnClickListener {
            binding.incomeButton.isChecked = true
            binding.expenseButton.isChecked = false
            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedRadio))
            expenseButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.unselectedRadio))
            incomeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            expenseButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))
        }

        expenseButton.setOnClickListener {
            expenseButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedRadio))
            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.unselectedRadio))
            expenseButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            incomeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))
        }

        binding.addButton.setOnClickListener {
            addExpenseToApi()
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

    private fun setupRecyclerView(categoryList: List<CategoriesListItem>) {
        _binding?.let { binding ->
            val adapter = CategoriesListAdapter(categoryList, imageMap) { selectedCategory ->
                binding.categoryField.setText(selectedCategory)
                binding.categoriesContainer.isVisible = false
            }
            binding.categoriesContainer.apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
            }
        }
    }
    private fun addExpenseToApi() {
        try{
        val amountString = binding.amountField.text.toString().trim()
        val note = binding.customTagsField.text.toString().trim()
        val date = binding.dateField.text.toString().trim()
        val time = binding.timeField.text.toString().trim()
        val category = binding.categoryField.text.toString().trim()
        val image = null
        val isCredit = binding.incomeButton.isChecked
        val amount = amountString.toDoubleOrNull()
        if (amount == null || category.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please fill amount and category fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AuthInstance.api.addExpense(amount.toString(), note, date, time, category, image, isCredit)
            .enqueue(object : Callback<AddExpenses> {
                override fun onResponse(call: Call<AddExpenses>, response: Response<AddExpenses>) {
                    if (isAdded) {
                        try {
                            if (response.isSuccessful && response.body() != null) {
                                Toast.makeText(
                                    requireContext(),
                                    "Record added successfully",
                                    Toast.LENGTH_LONG
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
                                "Error processing data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<AddExpenses>, t: Throwable) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "API call failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }catch (e: Exception){
            Toast.makeText(requireContext(),"Network Error",Toast.LENGTH_SHORT).show()
        }
    }
    private fun fetchCategories() {
        AuthInstance.api.getCategoryList().enqueue(object : Callback<com.example.xpensate.API.home.CategoryList.CategoriesList> {
            override fun onResponse(call: Call<com.example.xpensate.API.home.CategoryList.CategoriesList>, response: Response<com.example.xpensate.API.home.CategoryList.CategoriesList>) {
                if (response.isSuccessful) {
                    setupRecyclerView(response.body()!!)
                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<com.example.xpensate.API.home.CategoryList.CategoriesList>, t: Throwable) {
                context?.let {
                    Toast.makeText(
                        it,
                        "Failed to fetch categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
    private fun setupTimePicker() {
        val timeField = binding.timeField
        timeField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeField.setText(selectedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }
    }
    private fun setupDatePicker() {
        val dateField = binding.dateField
        dateField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                dateField.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}