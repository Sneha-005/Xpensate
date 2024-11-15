package com.example.xpensate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.xpensate.databinding.FragmentCurrencyConverterBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast


class  CurrencyConverter : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val from = view.findViewById<TextView>(R.id.from)
        val to = view.findViewById<TextView>(R.id.to)

        from.setOnClickListener {
            to.background = ContextCompat.getDrawable(requireContext(), R.drawable.currency_select)
            from.background = null
            childFragmentManager.commit {
                replace(R.id.currency_container, currency_converter_recycler())
            }
        }

        to.setOnClickListener {
            from.background = ContextCompat.getDrawable(requireContext(), R.drawable.currency_select)
            to.background = null
            childFragmentManager.commit {
                replace(R.id.currency_container, currency_converter_recycler())
            }
        }
       // fetchCurrencyData()
    }
   /* fun fetchCurrencyData() {
        AuthInstance.api.getCurrencies().enqueue(object : Callback<List<CurrencyItem>> {
            override fun onResponse(call: Call<List<CurrencyItem>>, response: Response<List<CurrencyItem>>) {
                if (response.isSuccessful) {
                    val currencyList = response.body() ?: emptyList()
                    val adapter = CurrencyAdapter(currencyList)
                    binding.currencyContainer.layoutManager = LinearLayoutManager(requireContext())
                    binding.currencyContainer.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CurrencyItem>>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }*/


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}