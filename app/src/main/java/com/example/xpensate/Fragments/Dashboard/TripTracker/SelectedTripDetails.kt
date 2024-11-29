package com.example.xpensate.Fragments.Dashboard.TripTracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.TripTracker.CreateGroupResponse.CreateGroupResponse
import com.example.xpensate.Adapters.TripTracker.SelectedTripDetailsAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentAddTripMemberBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectedTripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        selectedGroupId = arguments?.getString("groupId")
        selectedGroupName = arguments?.getString("name")

        adapter = SelectedTripDetailsAdapter(mutableListOf())
        binding.tripsContainer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectedTripDetails.adapter
        }

        binding.tripName.text = selectedGroupName

        binding.backArrow.setOnClickListener {
            navController.navigateUp()
        }
        binding.splitButton.setOnClickListener {
            val action = selectedGroupId?.let { it1 ->
                SelectedTripDetailsDirections.actionSelectedTripDetailsToAddSplit(
                    it1
                )
            }
            if (action != null) {
                navController.navigate(action)
            }
        }
        binding.payButton.setOnClickListener {
            val action = selectedGroupId?.let { it1 ->
                SelectedTripDetailsDirections.actionSelectedTripDetailsToAddTripMember(
                    it1
                )
            }
            if (action != null) {
                navController.navigate(action)
            }
        }

        binding.editButton.setOnClickListener {
            val action = selectedGroupId?.let { it1 ->
                SelectedTripDetailsDirections.actionSelectedTripDetailsToRemoveFromTrip(
                    it1
                )
            }
            if (action != null) {
                navController.navigate(action)
            }
        }
        fetchTripDetails()
    }

    private fun fetchTripDetails() {
        selectedGroupId?.let { groupId ->
            AuthInstance.api.getTripGroupDetails(groupId).enqueue(object :
                Callback<CreateGroupResponse> {
                override fun onResponse(
                    call: Call<CreateGroupResponse>,
                    response: Response<CreateGroupResponse>
                ) {
                    if (response.isSuccessful) {
                        val expenses = response.body()?.expenses ?: emptyList()
                        adapter.updateRecords(expenses)
                    } else {
                        Toast.makeText(context,"Failed to fetch group details",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CreateGroupResponse>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}