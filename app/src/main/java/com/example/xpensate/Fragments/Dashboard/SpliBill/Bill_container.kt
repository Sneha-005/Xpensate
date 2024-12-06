package com.example.xpensate.Fragments.Dashboard.SpliBill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.SplitAmountPage
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.SplitBillGroupShow
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.UpdateGroup
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.split_bill
import com.example.xpensate.R

class bill_container : Fragment() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bill_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_bill_container_to_blankFragment)
            }
        })

        val tabRecord = view.findViewById<TextView>(R.id.tab_record)
        val tabSplitBill = view.findViewById<TextView>(R.id.tab_split_bill)
        tabRecord.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)

        childFragmentManager.commit {
            replace(R.id.fragment_container, RecordEntry())
        }


        val newGroupButton = view.findViewById<Button>(R.id.new_group_button)
        newGroupButton?.setOnClickListener {
            if (tabSplitBill.background != null) {
                replaceFragmentWithUpdateGroup()
            }
        }

        tabRecord?.setOnClickListener {
            tabRecord.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
            tabSplitBill.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, RecordEntry())
            }
        }

        tabSplitBill?.setOnClickListener {
            tabSplitBill.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
            tabRecord.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, split_bill())
            }
        }
    }

    fun replaceFragmentWithUpdateGroup() {
        childFragmentManager.commit {
            replace(R.id.fragment_container, UpdateGroup())
            addToBackStack(null)
        }
    }

    fun replaceFragmentWithSplitAmountPage(groupId: String, name: String) {
        val fragment = SplitAmountPage().apply {
            arguments = Bundle().apply {
                putString("id", groupId)
                putString("name", name)
            }
        }
        childFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    fun replaceFragmentWithSplitBillGroupShow(groupId: String) {
        val fragment = SplitBillGroupShow().apply {
            arguments = Bundle().apply {
                putString("groupId", groupId)
            }
        }
        childFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    fun replaceFragmentWithSplitBill() {
        childFragmentManager.commit {
            replace(R.id.fragment_container, split_bill())
            addToBackStack(null)
        }
    }
}