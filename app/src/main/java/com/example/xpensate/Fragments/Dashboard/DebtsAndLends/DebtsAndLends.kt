package com.example.xpensate.Fragments.Dashboard.DebtsAndLends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentDebtsAndLendsBinding

class DebtsAndLends : Fragment() {

    private var _binding: FragmentDebtsAndLendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDebtsAndLendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogHelper.showProgressDialog(requireContext())

        binding.tabAdd.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
        childFragmentManager.commit {
            ProgressDialogHelper.hideProgressDialog()

            replace(R.id.fragment_container, DebtsAdd())
        }

        binding.tabAdd.setOnClickListener {
            binding.tabAdd.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
            binding.tabRecords.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, DebtsAdd())
            }
        }

        binding.tabRecords.setOnClickListener {
            binding.tabRecords.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_border)
            binding.tabAdd.background = null
            childFragmentManager.commit {
                replace(R.id.fragment_container, DebtsRecords())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
