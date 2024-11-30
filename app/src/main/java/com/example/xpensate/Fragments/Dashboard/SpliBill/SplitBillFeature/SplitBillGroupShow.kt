package com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.SplitBillFeature.SplitGroupShowDetails.SplitGroupDetails
import com.example.xpensate.Adapters.SlitBillFeature.SplitAmountShowAdapter
import com.example.xpensate.AuthInstance
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplitBillGroupShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        selectedGroupId = arguments?.getString("groupId")
        setupRecyclerView()
        fetchGroupData()
    }

    private fun setupRecyclerView() {
        adapter = SplitAmountShowAdapter(mutableListOf(), "")
        binding.splitContainer.layoutManager = LinearLayoutManager(context)
        binding.splitContainer.adapter = adapter
    }

    private fun fetchGroupData() {
        selectedGroupId?.let {
            AuthInstance.api.showSplitDetail(it).enqueue(object : Callback<SplitGroupDetails> {
                override fun onResponse(
                    call: Call<SplitGroupDetails>,
                    response: Response<SplitGroupDetails>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { details ->
                            binding.groupName.text = details.data.name
                            userName = details.data.groupowner
                            adapter.updateSplits(details.data.bills)
                        }
                    }
                }

                override fun onFailure(call: Call<SplitGroupDetails>, t: Throwable) {
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}