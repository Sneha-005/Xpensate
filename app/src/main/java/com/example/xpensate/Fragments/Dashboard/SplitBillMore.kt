package com.example.xpensate.Fragments.Dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.UpdateGroup
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.split_bill
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentSplitBillMoreBinding

class SplitBillMore : Fragment() {
    private var _binding: FragmentSplitBillMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplitBillMoreBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isAdded) {
                    navController.navigate(R.id.action_splitBillMore_to_blankFragment)
                }
            }
        })

        if (savedInstanceState == null) {
            safeReplaceFragment(split_bill())
        }
    }

    private fun safeReplaceFragment(fragment: Fragment) {
        if (isAdded) {
            childFragmentManager.commit {
                replace(R.id.fragment_container, fragment)
            }
        }
    }

    fun replaceFragmentWithUpdateGroup() {
        if (isAdded) {
            childFragmentManager.commit {
                replace(R.id.fragment_container, UpdateGroup())
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
