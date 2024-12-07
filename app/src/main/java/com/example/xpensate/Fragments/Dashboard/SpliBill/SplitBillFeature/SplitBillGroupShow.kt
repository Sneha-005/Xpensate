package com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.SplitBillFeature.Groups.MembersGroup
import com.example.xpensate.API.home.SplitBillFeature.Groups.OwnerGroup
import com.example.xpensate.API.home.SplitBillFeature.MarkPaidResponse
import com.example.xpensate.API.home.SplitBillFeature.SplitGroupShowDetails.SplitGroupDetails
import com.example.xpensate.Adapters.SlitBillFeature.SplitAmountShowAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.Fragments.Dashboard.SpliBill.bill_container
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.databinding.FragmentSplitBillGroupShowBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplitBillGroupShow : Fragment() {
    private var _binding: FragmentSplitBillGroupShowBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SplitAmountShowAdapter
    private var selectedGroupId: String? = null
    private lateinit var userName: String
    private lateinit var navController: NavController
    private var group: Any? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplitBillGroupShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        selectedGroupId = arguments?.getString("groupId")
        if (selectedGroupId == null) {
            Toast.makeText(context, "Group ID is not available", Toast.LENGTH_SHORT).show()
            return
        }
        setupRecyclerView()
        fetchGroupData()

        binding.backArrow.setOnClickListener {
            parentFragment?.childFragmentManager?.popBackStack()
        }

        binding.splitButton.setOnClickListener {
            group?.let { onSplitButtonClick(it) }
                ?: Toast.makeText(context, "Group data not loaded", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSplitButtonClick(group: Any) {
        Log.d("SplitBill", "Split button clicked for group: $group")
        val id = when (group) {
            is OwnerGroup -> group.id
            is MembersGroup -> group.id
            else -> ""
        }
        val name = when (group) {
            is OwnerGroup -> group.name
            is MembersGroup -> group.name
            else -> ""
        }
        (parentFragment as? bill_container)?.replaceFragmentWithSplitAmountPage(id.toString(), name)
    }

    private fun setupRecyclerView() {
        adapter = SplitAmountShowAdapter(mutableListOf(), "") { groupId, participantEmail, isPaid ->
            markBillAsPaid(selectedGroupId!!, participantEmail, isPaid)
        }
        binding.splitContainer.layoutManager = LinearLayoutManager(context)
        binding.splitContainer.adapter = adapter
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
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            selectedGroupId?.let { groupId ->
                AuthInstance.api.showSplitDetail(groupId).enqueue(object : Callback<SplitGroupDetails> {
                    override fun onResponse(
                        call: Call<SplitGroupDetails>,
                        response: Response<SplitGroupDetails>
                    ) {
                        if (response.isSuccessful) {
                            ProgressDialogHelper.hideProgressDialog()
                            response.body()?.let { details ->
                                if (_binding != null) {
                                    binding.groupName.text = details.data.name
                                    userName = details.data.groupowner
                                    adapter.updateSplits(details.data.bills)
                                    group = details.data
                                    binding.splitContainer.visibility = View.VISIBLE
                                    binding.noSplit.visibility = View.GONE
                                }
                            }
                        } else {
                            binding.splitContainer.visibility = View.GONE
                            binding.noSplit.visibility = View.VISIBLE
                            if(response.code() == 500){
                                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                            }
                            val errorBody = response.message().toString()
                            Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SplitGroupDetails>, t: Throwable) {
                        ProgressDialogHelper.hideProgressDialog()
                        context?.let {
                            Toast.makeText(it, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }
}
