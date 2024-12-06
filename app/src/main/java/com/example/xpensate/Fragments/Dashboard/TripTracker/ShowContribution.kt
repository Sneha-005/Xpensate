package com.example.xpensate.Fragments.Dashboard.TripTracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import com.example.xpensate.API.TripTracker.DeleteGroup
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentAddTripMemberBinding
import com.example.xpensate.databinding.FragmentShowContributionBinding
import com.example.xpensate.databinding.FragmentViewStatusBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowContribution : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentShowContributionBinding? = null
    private val binding get() = _binding!!
    private var selectedGroupId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowContributionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.deleteButton.setOnClickListener {
            selectedGroupId?.let {
                deleteGroup(it)
            }
        }

    }
    fun deleteGroup(id: String) {
        AuthInstance.api.deleteGroup(id).enqueue(object : Callback<DeleteGroup> {
            override fun onResponse(call: Call<DeleteGroup>, response: Response<DeleteGroup>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Group deleted successfully", Toast.LENGTH_SHORT).show()

                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteGroup>, t: Throwable) {
                println("Error deleting group: ${t.message}")
            }
        })
    }

}