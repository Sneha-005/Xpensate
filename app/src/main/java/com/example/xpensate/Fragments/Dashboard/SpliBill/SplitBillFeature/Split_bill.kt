package com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.SplitBillFeature.Groups.MembersGroup
import com.example.xpensate.API.home.SplitBillFeature.Groups.OwnerGroup
import com.example.xpensate.AuthInstance
import com.example.xpensate.Adapters.SplitBillFeature.SplitBillFeatureAdapter
import com.example.xpensate.Fragments.Dashboard.SpliBill.bill_container
import com.example.xpensate.Fragments.Dashboard.SpliBill.bill_containerDirections
import com.example.xpensate.Fragments.Dashboard.SplitBillMore
import com.example.xpensate.databinding.FragmentSplitBillBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class split_bill : Fragment() {

    private var _binding: FragmentSplitBillBinding? = null
    private val binding get() = _binding!!
    private lateinit var groupAdapter: SplitBillFeatureAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplitBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupAdapter = SplitBillFeatureAdapter(emptyList(), this)

        binding.splitBill.layoutManager = LinearLayoutManager(context)
        binding.splitBill.adapter = groupAdapter

        binding.newGroupButton.setOnClickListener {
            when (val parent = parentFragment) {
                is bill_container -> parent.replaceFragmentWithUpdateGroup()
                is SplitBillMore -> parent.replaceFragmentWithUpdateGroup()
                else -> {
                    // Handle case if parent is neither
                    Toast.makeText(requireContext(), "Unsupported parent fragment", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fetchGroupData()
    }

    fun onItemClick(group: Any){
        val id = when(group){
            is OwnerGroup -> group.id
            is MembersGroup -> group.id
            else -> ""
        }
        val action = bill_containerDirections.actionBillContainerToSplitBillGroupShow(id.toString())
        findNavController().navigate(action)
    }
    fun onSplitButtonClick(group: Any) {
        Log.d("SplitBill", "Split button clicked for group: $group")
        val id = when (group) {
            is OwnerGroup -> group.id
            is MembersGroup -> group.id
            else -> ""
        }
        val action = bill_containerDirections.actionBillContainerToSplitAmountPage2(id.toString())
        findNavController().navigate(action)
    }

    fun onAddMemberButtonClick(group: Any) {
        Log.d("SplitBill", "Add member button clicked for group: $group")
        val id = when (group) {
            is OwnerGroup -> group.id
            is MembersGroup -> group.id
            else -> ""
        }
        val action = bill_containerDirections.actionBillContainerToAddMember(id.toString())
        findNavController().navigate(action)
    }

    private fun fetchGroupData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = AuthInstance.api.getGroups().execute()
                    if (response.isSuccessful) {
                        val groupsResponse = response.body()
                        withContext(Dispatchers.Main) {
                            if (groupsResponse != null) {
                                Log.d("SplitBill", "Fetched groups: $groupsResponse")
                                val combinedGroups = (groupsResponse.ownerGroups ?: emptyList()) + (groupsResponse.membersGroups ?: emptyList())
                                updateGroupData(combinedGroups)
                            } else {
                                Log.e("SplitBill", "Response body is null")
                            }
                        }
                    } else {
                        Log.e("SplitBill", "Response code: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SplitBill", "Failed to fetch data", e)
                withContext(Dispatchers.Main) {
                    context?.let {
                        Toast.makeText(it, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateGroupData(newData: List<Any>) {
        groupAdapter.updateData(newData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}