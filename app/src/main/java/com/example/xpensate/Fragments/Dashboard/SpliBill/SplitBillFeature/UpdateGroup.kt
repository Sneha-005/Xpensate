package com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.SplitBillFeature.CreateGroup
import com.example.xpensate.Adapters.OverlappingImagesAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.Fragments.Dashboard.SpliBill.bill_container
import com.example.xpensate.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.xpensate.databinding.FragmentUpdateGroupBinding

class UpdateGroup : Fragment() {

    private lateinit var binding: FragmentUpdateGroupBinding
    private lateinit var overlappingImagesAdapter: OverlappingImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateGroupBinding.inflate(inflater, container, false)

        overlappingImagesAdapter = OverlappingImagesAdapter(
            listOf(R.drawable.avatar1, R.drawable.avatar3)
        )

        binding.addMemberButton.setOnClickListener {
            findNavController().navigate(R.id.action_bill_container_to_addMember)
        }
        val groupName = binding.name.text

        binding.createGroupButton.setOnClickListener {
            createGroup(groupName.toString(),"")
            (parentFragment as? bill_container)?.replaceFragmentWithSplitBill()
        }

        binding.overlappingImagesRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = overlappingImagesAdapter
        }

        return binding.root
    }
    private fun createGroup(groupName: String, memberEmail: String) {
        AuthInstance.api.addGroup(groupName,memberEmail).enqueue(object : Callback<CreateGroup> {
            override fun onResponse(call: Call<CreateGroup>, response: Response<CreateGroup>) {
                if (response.isSuccessful) {
                    Toast.makeText(context,"Group created Successfully",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context,"Group creation failed",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateGroup>, t: Throwable) {
               Toast.makeText(context,"Try after some time",Toast.LENGTH_SHORT).show()
            }
        })
    }
}
