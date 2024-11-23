package com.example.xpensate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.xpensate.Fragments.Dashboard.SpliBill.RecordEntry

class DebtsAndLends : Fragment() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debts_and_lends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val tabAdd = view.findViewById<TextView>(R.id.tab_add)
        val tabRecords = view.findViewById<TextView>(R.id.tab_records)
        tabAdd.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)

        childFragmentManager.commit {
            replace(R.id.fragment_container, RecordEntry())
        }

        tabAdd.setOnClickListener {
            tabAdd.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.bottom_border
            )
            tabRecords.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, DebtsAdd())
            }

        }

        tabRecords.setOnClickListener {
            tabRecords.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.bottom_border
            )
            tabAdd.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, DebtsRecords())
            }

        }

    }
}