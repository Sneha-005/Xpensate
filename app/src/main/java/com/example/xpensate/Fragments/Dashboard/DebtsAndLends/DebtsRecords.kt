package com.example.xpensate.Fragments.Dashboard.DebtsAndLends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.DebtsAndLends.DebtsData
import com.example.xpensate.API.home.DebtsAndLends.DebtsList
import com.example.xpensate.Adapters.DebtsAndLends.DebtsAdapter
import com.example.xpensate.Adapters.DebtsAndLends.LendsAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentDebtsRecordsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.ProgressDialog
import com.example.xpensate.ProgressDialogHelper

class DebtsRecords : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentDebtsRecordsBinding? = null
    private val binding get() = _binding!!

    private val debtsAdapter = DebtsAdapter(mutableListOf())
    private val lendsAdapter = LendsAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebtsRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setupRecyclerViews()
        fetchDebtsData()
        setupOnBackPressedCallback()
    }

    private fun setupRecyclerViews() {
        binding.debtsContainer.apply {
            adapter = debtsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.lendsContainer.apply {
            adapter = lendsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchDebtsData() {
        ProgressDialogHelper.showProgressDialog(requireContext())
        try {
            AuthInstance.api.getDebts().enqueue(object : Callback<DebtsData> {
                override fun onResponse(call: Call<DebtsData>, response: Response<DebtsData>) {
                    ProgressDialogHelper.hideProgressDialog()
                    try {
                        if (response.isSuccessful) {
                            response.body()?.data?.let { debts: List<DebtsList> ->
                                debtsAdapter.updateRecords(debts)
                                lendsAdapter.updateRecords(debts)
                            }
                        } else {
                            context?.let {
                                if(response.code() == 500){
                                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                                }
                                val errorBody = response.message().toString()
                                Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DebtsRecords", "Error parsing response: ${e.message}")
                        context?.let {
                            Toast.makeText(it, "An error occurred while processing data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<DebtsData>, t: Throwable) {
                    ProgressDialogHelper.hideProgressDialog()
                    Log.e("DebtsRecords", "API call failed: ${t.message}")
                    context?.let {
                        Toast.makeText(it, "API call failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } catch (e: Exception) {
            ProgressDialogHelper.hideProgressDialog()
            Log.e("DebtsRecords", "Error initiating API call: ${e.message}")
            context?.let {
                Toast.makeText(it, "Failed to initiate API call", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOnBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    navController.navigate(R.id.action_debtsAndLends_to_blankFragment)
                } catch (e: Exception) {
                    Log.e("DebtsRecords", "Error navigating back: ${e.message}")
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
