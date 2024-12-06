package com.example.xpensate.Fragments.Dashboard.TripTracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.TripTracker.CreateGroupResponse.CreateGroupResponse
import com.example.xpensate.Adapters.TripTracker.SelectedTripDetailsAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentSelectedTripDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectedTripDetails : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentSelectedTripDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SelectedTripDetailsAdapter
    private var selectedGroupId: String? = null
    private var selectedGroupName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedTripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        initArgs()
        setupUI()
        setupRecyclerView()
        fetchTripDetails()
        binding.backArrow.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun initArgs() {
        selectedGroupId = arguments?.getString("groupId")
        selectedGroupName = arguments?.getString("name")
        binding.tripName.text = selectedGroupName
    }

    private fun setupUI() {
        binding.backArrow.setOnClickListener { findNavController().navigateUp() }

        binding.splitButton.setOnClickListener {
            selectedGroupId?.let {
                val action = SelectedTripDetailsDirections.actionSelectedTripDetailsToAddSplit(it)
                findNavController().navigate(action)
            }
        }

        binding.viewStatus.setOnClickListener {
            selectedGroupId?.let {
                childFragmentManager.commit {
                    replace(R.id.trips_container, ViewStatus())
                }
            }
        }
        binding.payButton.setOnClickListener {
            selectedGroupId?.let {
                // Uncomment and add appropriate navigation logic
                // val action = SelectedTripDetailsDirections.actionSelectedTripDetailsToAddTripMember(it)
                // findNavController().navigate(action)
            }
        }

        binding.editButton.setOnClickListener {
            selectedGroupId?.let {
                val action = SelectedTripDetailsDirections.actionSelectedTripDetailsToRemoveFromTrip(it)
                findNavController().navigate(action)
            }
        }
    }

    private fun setupRecyclerView() {
        Log.e("recycle","set up")
        adapter = SelectedTripDetailsAdapter(mutableListOf())
        binding.tripsContainer.layoutManager = LinearLayoutManager(context)
        binding.tripsContainer.adapter = adapter
    }

    private fun fetchTripDetails() {
        selectedGroupId?.let { groupId ->
            AuthInstance.api.getTripGroupDetails(groupId).enqueue(object : Callback<CreateGroupResponse> {
                override fun onResponse(
                    call: Call<CreateGroupResponse>,
                    response: Response<CreateGroupResponse>
                ) {
                    if (response.isSuccessful) {
                        val expenses = response.body()?.expenses.orEmpty()
                        adapter.updateRecords(expenses)
                    } else {
                        if(response.code() == 500){
                            Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                        }
                        val errorBody = response.message().toString()
                        Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CreateGroupResponse>, t: Throwable) {
                    showToast("Network issue")
                }
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
