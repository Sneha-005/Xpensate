package com.example.xpensate.Fragments.Dashboard

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.CategoryList.CategoriesList
import com.example.xpensate.API.home.CategoryList.CategoriesListItem
import com.example.xpensate.API.home.DeleteRecords
import com.example.xpensate.API.home.RecordDetails
import com.example.xpensate.API.home.UpdateRecords
import com.example.xpensate.Adapters.CategoriesListAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.Fragments.Dashboard.SpliBill.bill_containerDirections
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentDeleteRecordBinding
import com.example.xpensate.databinding.FragmentRecordEntryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale

class DeleteRecord : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentDeleteRecordBinding? = null
    private val binding get() = _binding!!
    private var selectedId: String? = null
    private val imageMap = mapOf(
        "Food" to R.drawable.food_icon,
        "Housing" to R.drawable.housing_icon,
        "Shopping" to R.drawable.shopping_icon,
        "Investment" to R.drawable.investment_icon,
        "Life & Entertainment" to R.drawable.life_icon,
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
        _binding = FragmentDeleteRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setupDatePicker()
        setupTimePicker()
        selectedId = arguments?.getString("id")
        if (selectedId == null) {
            Toast.makeText(requireContext(), "Invalid record ID", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            return
        }

        fetchRecordsDetails(selectedId!!)
        binding.categoryList.isVisible = false
        fetchCategories()
        binding.categoryField.setOnClickListener {
            binding.categoryList.isVisible = !binding.categoryList.isVisible
        }
        binding.deleteButton.setOnClickListener {
            selectedId?.let { deleteRecords(it) }
        }

        binding.saveButton.setOnClickListener {
            selectedId?.let { updateRecordDetails(it) }
        }
    }

    private fun fetchRecordsDetails(id: String){
        AuthInstance.api.getRecordDetails(id).enqueue(object: Callback<RecordDetails> {
            override fun onResponse(call: Call<RecordDetails>, response: Response<RecordDetails>){
                if(response.isSuccessful){
                    val response = response.body()
                    val incomeButton: RadioButton = binding.incomeButton
                    val expenseButton: RadioButton = binding.expenseButton
                    if (response != null) {
                        binding.amountField.setText(response.amount)
                        binding.categoryField.setText(response.category)
                        binding.customTagsField.setText(response.note)
                        val dateOnly = response.date.split(" ")[0]
                        binding.dateField.setText(dateOnly)
                        binding.timeField.setText(formatTime(response.time))
                        if(response.is_credit){
                            binding.incomeButton.isChecked = true
                            binding.expenseButton.isChecked = false
                            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedRadio))
                            expenseButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.unselectedRadio))
                            incomeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                            expenseButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))
                        }
                        else{
                            expenseButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedRadio))
                            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.unselectedRadio))
                            expenseButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                            incomeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font_color))

                        }

                    }
                }
                else{
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<RecordDetails>, t: Throwable) {
                Log.e("CategoriesFragment", "Error: ${t.message}")
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecordDetails(id: String){
        val amount = binding.amountField.text.toString().trim()
        val note = binding.customTagsField.text.toString().trim()
        val date = binding.dateField.text.toString().trim()
        val time = binding.timeField.text.toString().trim()
        val category = binding.categoryField.text.toString().trim()
        val image = null
        val isCredit = binding.incomeButton.isChecked
        if (amount.isEmpty()|| category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill amount and category fields", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            ProgressDialogHelper.showProgressDialog(requireContext())

        AuthInstance.api.updateRecords(id,amount, note, date, time, category, image, isCredit).enqueue(object: Callback<UpdateRecords> {
            override fun onResponse(call: Call<UpdateRecords>, response: Response<UpdateRecords>){
                Log.d("update","Response: ${response.code()}")
                Log.d("update","response: ${response.body()}")
                if(response.isSuccessful){
                    Toast.makeText(requireContext(), "Record updated successfully", Toast.LENGTH_LONG).show()
                }else {
                   if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UpdateRecords>, t: Throwable) {
                ProgressDialogHelper.hideProgressDialog()
                Log.e("CategoriesFragment", "Error: ${t.message}")
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
        } catch (e: Exception) {
            Log.e("UpdateRecord", "Error: ${e.message}")
        }finally {
            ProgressDialogHelper.hideProgressDialog()

        }
    }

    private fun deleteRecords(id: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Record")
            .setMessage("Are you sure you want to delete this record?")
            .setPositiveButton("Yes") { _, _ ->
                ProgressDialogHelper.showProgressDialog(requireContext())
                AuthInstance.api.deleteRecords(id).enqueue(object : Callback<DeleteRecords> {
                    override fun onResponse(call: Call<DeleteRecords>, response: Response<DeleteRecords>) {
                        Log.d("DeletedRecords","Response: ${response.code()}")
                        Log.d("DeletedRecords","GroupId: ${id}")

                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            if(response.code() == 500){
                                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                            }
                            val errorBody = response.message().toString()
                            Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DeleteRecords>, t: Throwable) {
                        ProgressDialogHelper.hideProgressDialog()
                        Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()

                    }
                })
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun formatTime(timeString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(timeString)
            outputFormat.format(date)
        } catch (e: Exception) {
            "Invalid time"
        }
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
    private fun fetchCategories() {
        ProgressDialogHelper.showProgressDialog(requireContext())
        AuthInstance.api.getCategoryList().enqueue(object : Callback<CategoriesList> {
            override fun onResponse(call: Call<CategoriesList>, response: Response<CategoriesList>) {
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

            override fun onFailure(call: Call<CategoriesList>, t: Throwable) {
                ProgressDialogHelper.hideProgressDialog()
                Log.e("CategoriesFragment", "Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}