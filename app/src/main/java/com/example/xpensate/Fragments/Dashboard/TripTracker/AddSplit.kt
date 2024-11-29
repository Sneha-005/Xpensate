package com.example.xpensate.Fragments.Dashboard.TripTracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.API.TripTracker.AddTripExpense
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentAddSplitBinding
import com.example.xpensate.databinding.FragmentAddTripMemberBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddSplit: Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentAddSplitBinding? = null
    private val binding get() = _binding!!
    private var selectedGroupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentAddSplitBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        val paidBy = binding.name.text.toString()
        val amount = binding.amount.text.toString().toDouble()
        val whatFor = binding.paidFor.text.toString()
        val tripId = selectedGroupId?.toInt()
        binding.splitButton.setOnClickListener {
            if (tripId != null) {
                addExpense(tripId,amount,paidBy,whatFor)
            }

        }


    }
    private fun addExpense(tripId: Int, amount: Double, paidBy: String, whatFor: String) {
        AuthInstance.api.addExpense(tripId, amount, paidBy, whatFor).enqueue(object :
            Callback<AddTripExpense> {
            override fun onResponse(call: Call<AddTripExpense>, response: Response<AddTripExpense>) {
                if (response.isSuccessful) {
                    Log.d("AddExpense", "Expense added successfully: ${response.body()}")
                    Toast.makeText(requireContext(),"Record added successfully",Toast.LENGTH_SHORT).show()
                } else {
                    // Handle API error
                    Log.e("AddExpense", "Failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AddTripExpense>, t: Throwable) {
                // Handle failure
                Log.e("AddExpense", "Error: ${t.message}")
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}