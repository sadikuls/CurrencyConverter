package com.sadikul.currencyconverter.ui.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.sadikul.currencyconverter.BuildConfig
import com.sadikul.currencyconverter.R
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.databinding.FragmentConverterBinding
import com.sadikul.currencyconverter.ui.view.adapter.CurrencyAdapter
import com.sadikul.currencyconverter.ui.view.viewmodel.CurrencyViewModel
import com.sadikul.currencyconverter.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConverterFragment : Fragment() {
    private val currencyViewModel: CurrencyViewModel by viewModels()
    private val currencyList: MutableList<CurrencyEntity> by lazy { ArrayList<CurrencyEntity>() }
    private lateinit var _binding: FragmentConverterBinding
    private val TAG = ConverterFragment::class.java.simpleName
    private lateinit var currencyAdapter: CurrencyAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentConverterBinding.bind(view)
        _binding.llSelectCurrency.setOnClickListener({
            currencyViewModel.getDataFromSever(BuildConfig.API_KEY,"USD","1")
        })
        setupObserver()
        setupRecyclerView()
    }

    private fun setupObserver() {
        Log.d(TAG, " currency-app setupObserver")
        currencyViewModel.data.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        updateList(list)
                    }
                }

                Status.LOADING -> {
                    //showLoader()
                    //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "currency-app loading started")
                }

                Status.ERROR -> {
                    //hideLoader(false)
                    //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "currency-app got error")
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 3)
        _binding.recyclerView.apply {
            currencyAdapter = CurrencyAdapter(currencyList){ item ->

            }
            layoutManager = gridLayoutManager
            adapter = currencyAdapter
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_converter, container, false)
    }

    private fun updateList(currencies: List<CurrencyEntity>) {
        _binding.recyclerView.apply {
            if(visibility != View.VISIBLE) visibility = View.VISIBLE
        }
        currencies.let {
            currencyList.apply {
                //clear()
                addAll(it)
                currencyAdapter.notifyDataSetChanged()
                Log.d("updateData", "Yes ${currencies.size}")
            }
        }
    }
}