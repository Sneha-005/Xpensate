package com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.SplitBillFeature.BillParticipantX
import com.example.xpensate.API.home.SplitBillFeature.CreateBillRequest
import com.example.xpensate.API.home.SplitBillFeature.CreateBillResponse
import com.example.xpensate.API.home.SplitBillFeature.GroupMembers.GroupMembers
import com.example.xpensate.API.home.SplitBillFeature.MarkPaidResponse
import com.example.xpensate.Adapters.OverlappingImagesAdapter
import com.example.xpensate.Adapters.SlitBillFeature.SplitAmountpageAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.Fragments.Dashboard.SpliBill.bill_container
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentSplitAmountPageBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplitAmountPage : Fragment() {

    private var _binding: FragmentSplitAmountPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SplitAmountpageAdapter
    private val participants = mutableListOf<BillParticipantX>()
    private var selectedGroupId: String? = null
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplitAmountPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        binding.backArrow.setOnClickListener {
            (parentFragment as? bill_container)?.replaceFragmentWithSplitBill()
        }

        selectedGroupId = arguments?.getString("id")
        val groupName = arguments?.getString("name")
        if (selectedGroupId == null) {
            Toast.makeText(context, "Group ID is not available", Toast.LENGTH_SHORT).show()
            return
        }

        binding.groupName.text = groupName
        adapter = SplitAmountpageAdapter(participants, selectedGroupId ?: "", ::markBillAsPaid)
        binding.contactContainer.layoutManager = LinearLayoutManager(context)
        binding.contactContainer.adapter = adapter

        fetchGroupData()

        binding.splitButton.setOnClickListener {
            createBill(adapter)
        }
    }
    private fun markBillAsPaid(groupId: String, participantEmail: String, isPaid: Boolean) {
        if (isPaid) {
            AuthInstance.api.markBillAsPaid(groupId, participantEmail)
                .enqueue(object : Callback<MarkPaidResponse> {
                    override fun onResponse(call: Call<MarkPaidResponse>, response: Response<MarkPaidResponse>) {
                        if (response.isSuccessful) {
                            val markPaidResponse = response.body()
                            if (markPaidResponse?.success == "true") {
                                Toast.makeText(context, "Bill marked as paid", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "${markPaidResponse?.success}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if(response.code() == 500){
                                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                            }
                            val errorBody = response.message().toString()
                            Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<MarkPaidResponse>, t: Throwable) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchGroupData() {
        ProgressDialogHelper.showProgressDialog(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            selectedGroupId?.let {
                AuthInstance.api.getGroupMembers(it).enqueue(object : Callback<GroupMembers> {
                    override fun onResponse(
                        call: Call<GroupMembers>,
                        response: Response<GroupMembers>
                    ) {
                        if (response.isSuccessful) {
                            ProgressDialogHelper.hideProgressDialog()
                            val groupMembers = response.body()?.data ?: emptyList()
                            if (groupMembers.isNotEmpty()) {
                                participants.clear()
                                participants.addAll(groupMembers.map { member ->
                                    BillParticipantX(
                                        amount = "0",
                                        participant = member.member.email
                                    )
                                })
                                adapter.notifyDataSetChanged()
                                val imageList = groupMembers.map {
                                    it.member.profile_image as? Int ?: R.drawable.avatar3
                                }
                                binding.overlappingImagesRecycler.layoutManager =
                                    LinearLayoutManager(
                                        context, LinearLayoutManager.HORIZONTAL, false
                                    )
                                val overlappingAdapter = OverlappingImagesAdapter(imageList)
                                binding.overlappingImagesRecycler.adapter = overlappingAdapter
                                binding.contactContainer.visibility = View.VISIBLE
                                binding.noSplit.visibility = View.GONE
                            } else {
                                binding.contactContainer.visibility = View.GONE
                                binding.noSplit.visibility = View.VISIBLE
                                Toast.makeText(
                                    context,
                                    "No participants found in the group",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Log.d("Split", "Group members: $groupMembers")
                        } else {
                            if(response.code() == 500){
                                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                            }
                            else {
                                val errorBody = response.message()?.toString()
                                Toast.makeText(requireContext(), "$errorBody", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GroupMembers>, t: Throwable) {
                        ProgressDialogHelper.hideProgressDialog()
                        Toast.makeText(
                            context,
                            "Failed to fetch data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } ?: run {
                Toast.makeText(context, "Group ID is not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createBill(adapter: SplitAmountpageAdapter) {
        val currentParticipants = adapter.items.filter { it.amount.isNotBlank() }

        if (currentParticipants.isEmpty()) {
            Toast.makeText(requireContext(), "No participants with valid amounts", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedParticipants = currentParticipants.map {
            BillParticipantX(
                amount = it.amount,
                participant = it.participant
            )
        }

        val request = CreateBillRequest(
            amount = binding.amount.text.toString(),
            bill_participants = selectedParticipants,
            group = selectedGroupId ?: ""
        )
Log.d("tfvh","$request")
        AuthInstance.api.createBill(request).enqueue(object : Callback<CreateBillResponse> {
            override fun onResponse(call: Call<CreateBillResponse>, response: Response<CreateBillResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Bill created successfully", Toast.LENGTH_SHORT).show()
                    parentFragment?.childFragmentManager?.popBackStack()

                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateBillResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}