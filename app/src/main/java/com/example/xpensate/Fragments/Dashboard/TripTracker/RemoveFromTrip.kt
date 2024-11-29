package com.example.xpensate.Fragments.Dashboard.TripTracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.TripTracker.CreateGroupResponse.CreateGroupResponse
import com.example.xpensate.Adapters.TripTracker.RemoveTripAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.databinding.FragmentRemoveFromTripBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoveFromTrip : Fragment() {
    private var _binding: FragmentRemoveFromTripBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RemoveTripAdapter
    private var selectedGroupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemoveFromTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedGroupId = arguments?.getString("groupId")

        if (selectedGroupId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Group ID not provided", Toast.LENGTH_SHORT).show()
            return
        }

        adapter = RemoveTripAdapter(
            memberList = emptyList(),
            groupId = selectedGroupId!!,
            context = requireContext(),
            onMemberRemoved = { fetchGroupData() }
        )

        binding.tripsContainer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RemoveFromTrip.adapter
        }

        fetchGroupData()
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
