package com.example.xpensate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import split_bill

class bill_container : Fragment() {

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
        val tabRecord = view.findViewById<TextView>(R.id.tab_record)
        val tabSplitBill = view.findViewById<TextView>(R.id.tab_split_bill)
        tabRecord.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)

        childFragmentManager.commit {
            replace(R.id.fragment_container, RecordEntry())
        }

        tabRecord.setOnClickListener {
            tabRecord.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
            tabSplitBill.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, RecordEntry())
            }

        }

        tabSplitBill.setOnClickListener {
            tabSplitBill.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
            tabRecord.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, split_bill())
            }

        }

    }
}
