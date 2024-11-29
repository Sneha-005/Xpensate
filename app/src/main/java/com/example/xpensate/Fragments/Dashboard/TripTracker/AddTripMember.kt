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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.TripTracker.AddTripMemberResponse
import com.example.xpensate.API.TripTracker.CreateGroupResponse.CreateGroupResponse
import com.example.xpensate.Adapters.TripTracker.AddTripAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.databinding.FragmentAddTripMemberBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTripMember : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentAddTripMemberBinding? = null
    private val binding get() = _binding!!
    private var selectedGroupId: String? = null
    private lateinit var adapter: AddTripAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTripMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        selectedGroupId = arguments?.getString("GROUP_ID")
        if (selectedGroupId == null) {
            Toast.makeText(context, "Group ID is missing", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
            return
        }

        binding.joinButton.setOnClickListener {
            val newMemberEmail = binding.email.text.toString().trim()
            if (newMemberEmail.isNotEmpty()) {
                addTripMember(selectedGroupId!!, newMemberEmail)
            } else {
                Toast.makeText(requireContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        adapter = AddTripAdapter(
            memberList = emptyList(),
            context = requireContext()
        )
        binding.tripsContainer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AddTripMember.adapter
        }
        fetchGroupData()
    }

    private fun addTripMember(groupId: String, email: String) {
        AuthInstance.api.addTripMember(groupId, email).enqueue(object : Callback<AddTripMemberResponse> {
            override fun onResponse(call: Call<AddTripMemberResponse>, response: Response<AddTripMemberResponse>) {
                if (response.isSuccessful && response.body()?.success == "true") {
                    Toast.makeText(context, "Member added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("AddTripMember", "Error: ${response.code()}")
                    Toast.makeText(context, "Failed to add member: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddTripMemberResponse>, t: Throwable) {
                Log.e("AddTripMember", "API Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun fetchGroupData() {
        selectedGroupId?.let { groupId ->
            AuthInstance.api.getTripGroupDetails(groupId).enqueue(object : Callback<CreateGroupResponse> {
                override fun onResponse(call: Call<CreateGroupResponse>, response: Response<CreateGroupResponse>) {
                    if (response.isSuccessful) {
                        val groupMembers = response.body()?.group?.members ?: emptyList()
                        if (groupMembers.isEmpty()) {
                            Toast.makeText(context, "No members in this group", Toast.LENGTH_SHORT).show()
                        }
                        adapter.updateData(groupMembers)
                        Log.d("RemoveFromTrip", "Fetched members: $groupMembers")
                    } else {
                        Log.e("RemoveFromTrip", "Error: ${response.code()}")
                        Toast.makeText(context, "Failed to fetch group details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CreateGroupResponse>, t: Throwable) {
                    Log.e("RemoveFromTrip", "API Error: ${t.message}")
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(context, "Group ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
