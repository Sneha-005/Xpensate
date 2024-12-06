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
        arguments?.let {
            selectedGroupId = it.getString("groupId")
        }
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
        val input = binding.amount.text.toString()
        val amount = input.toDoubleOrNull()?: 0.0
        val whatFor = binding.paidFor.text.toString()
        val tripId = selectedGroupId?.toInt()
        binding.splitButton.setOnClickListener {
            Log.d("Groupid","$tripId")
            if (tripId != null) {
                addExpense(tripId,amount,paidBy,whatFor)
            }
            else{
                Toast.makeText(context,"group id is not provided",Toast.LENGTH_SHORT).show()
            }

        }
        binding.backArrow.setOnClickListener {
            navController.navigateUp()
        }


    }
    private fun addExpense(tripId: Int, amount: Double, paidBy: String, whatFor: String) {
        AuthInstance.api.addExpense(tripId, amount, paidBy, whatFor).enqueue(object :
            Callback<AddTripExpense> {
            override fun onResponse(call: Call<AddTripExpense>, response: Response<AddTripExpense>) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        Log.d("AddExpense", "Expense added successfully: ${response.body()}")
                        Toast.makeText(
                            requireContext(),
                            "Record added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigateUp()
                    } else {
                        if (response.code() == 500) {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val errorBody = response.message().toString()
                        Toast.makeText(requireContext(), "$errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AddTripExpense>, t: Throwable) {
                Log.e("AddExpense", "Error: ${t.message}")
                Toast.makeText(requireContext(),"Network issue",Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}