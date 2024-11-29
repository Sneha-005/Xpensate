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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_debtsAndLends_to_blankFragment)
            }
        })
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
        AuthInstance.api.getDebts().enqueue(object : Callback<DebtsData> {
            override fun onResponse(call: Call<DebtsData>, response: Response<DebtsData>) {
                Log.d("Debt", response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.data?.let { debts: List<DebtsList> ->
                        debtsAdapter.updateRecords(debts)
                        lendsAdapter.updateRecords(debts)
                    }
                } else {
                    context?.let {
                        Toast.makeText(it, "Failed to fetch debts data", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<DebtsData>, t: Throwable) {
                context?.let {
                    Toast.makeText(it, "API call failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
