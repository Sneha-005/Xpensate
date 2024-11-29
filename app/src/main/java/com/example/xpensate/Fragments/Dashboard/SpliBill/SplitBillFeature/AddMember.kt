package com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature

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
import com.example.xpensate.API.home.SplitBillFeature.AddMember
import com.example.xpensate.API.home.SplitBillFeature.DeleteMember
import com.example.xpensate.API.home.SplitBillFeature.GroupMembers.Data
import com.example.xpensate.API.home.SplitBillFeature.GroupMembers.GroupMembers
import com.example.xpensate.AuthInstance
import com.example.xpensate.Adapters.SlitBillFeature.AddmemberAdapter
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentAddMemberBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMember : Fragment() {

    private var _binding: FragmentAddMemberBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AddmemberAdapter
    private var selectedGroupId: String? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        selectedGroupId = arguments?.getString("groupId")
        Log.d("AddMember", "Selected group ID: $selectedGroupId")
        fetchGroupData()

        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.addButton.setOnClickListener {
            val memberEmail = binding.email.text.toString().trim()
            if (memberEmail.isNotEmpty()) {
                selectedGroupId?.let { groupId ->
                    addMemberToGroup(groupId, memberEmail)
                } ?: run {
                    Toast.makeText(context, "Please select a group", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }
        }
        binding.contactContainer.layoutManager = LinearLayoutManager(requireContext())
        adapter = AddmemberAdapter(emptyList(), onMemberRemoved = { email ->
            selectedGroupId?.let { groupId ->
                Log.d("Split","group id: $groupId")
                Log.d("Split","email: $email")
                removeGroupMember(groupId, email)
            }
        })
        binding.contactContainer.adapter = adapter
    }

    private fun removeGroupMember(groupId: String, memberEmail: String) {
        AuthInstance.api.removeMember(groupId, memberEmail).enqueue(object : Callback<DeleteMember> {
            override fun onResponse(call: Call<DeleteMember>, response: Response<DeleteMember>) {
                Log.d("Split","group id: $groupId")
                Log.d("Split","email: $memberEmail")
                if (response.isSuccessful) {
                    Toast.makeText(context, "Member removed successfully", Toast.LENGTH_SHORT).show()
                    fetchGroupData()
                } else {
                    Toast.makeText(context, "Failed to remove member: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteMember>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun fetchGroupData() {
        selectedGroupId?.let {
            AuthInstance.api.getGroupMembers(it).enqueue(object : Callback<GroupMembers> {
                override fun onResponse(call: Call<GroupMembers>, response: Response<GroupMembers>) {
                    if (response.isSuccessful) {
                        val groupMembers = response.body()?.data ?: emptyList()
                        adapter.updateData(groupMembers)
                        Log.d("Split", "Group members: $groupMembers")
                    } else {
                        Log.e("Split", "Failed to fetch group: ${response.message()}")
                        Toast.makeText(context, "Failed to fetch group", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GroupMembers>, t: Throwable) {
                    Log.e("Split", "Failed to fetch data: ${t.message}")
                    Toast.makeText(context, "Failed to fetch data: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun addMemberToGroup(groupId: String, memberEmail: String) {
        AuthInstance.api.addMemberToGroup(groupId, memberEmail).enqueue(object : Callback<AddMember> {
            override fun onResponse(call: Call<AddMember>, response: Response<AddMember>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Member added successfully!", Toast.LENGTH_SHORT).show()
                    fetchGroupData()
                } else {
                    Log.e("API Error", "Failed to add member: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to add member: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddMember>, t: Throwable) {
                Log.e("API Error", "Network failure: ${t.message}")
                Toast.makeText(context, "Network failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}