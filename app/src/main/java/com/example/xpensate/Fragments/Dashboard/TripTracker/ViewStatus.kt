package com.example.xpensate.Fragments.Dashboard.TripTracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentAddSplitBinding
import com.example.xpensate.databinding.FragmentViewStatusBinding

class ViewStatus : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentViewStatusBinding? = null
    private val binding get() = _binding!!
    private var selectedGroupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedGroupId = it.getString("groupId")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewStatusBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

}