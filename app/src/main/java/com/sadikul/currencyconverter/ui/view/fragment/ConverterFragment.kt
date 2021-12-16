package com.sadikul.currencyconverter.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.sadikul.currencyconverter.R
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.databinding.FragmentConverterBinding
import com.sadikul.currencyconverter.ui.view.adapter.CurrencyAdapter
import com.sadikul.currencyconverter.ui.view.viewmodel.CurrencyViewModel
import com.sadikul.currencyconverter.utils.Status
import com.sadikul.currencyconverter.worker.ServerDataReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class ConverterFragment : Fragment() {
    private val currencyViewModel: CurrencyViewModel by viewModels()
    private val currencyList = mutableListOf<CurrencyEntity>()
    private lateinit var _binding: FragmentConverterBinding
    private val TAG = ConverterFragment::class.java.simpleName
    private lateinit var currencyAdapter: CurrencyAdapter
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresStorageNotLow(true)
        .setRequiresBatteryNotLow(true)
        .build()
    private val workManager by lazy {
        WorkManager.getInstance(activity?.applicationContext!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentConverterBinding.bind(view)
        currencyViewModel.getData("USDUSD", "1")
        _binding.llSelectCurrency.setOnClickListener({
            currencyViewModel.getData("USDBDT", "150")
        })
        setupObserver()
        setupRecyclerView()
        createPeriodicWorkRequest()
    }

    private fun setupSpinner(list: MutableMap<String, Double>) {
// Create an ArrayAdapter using the string array and a default spinner layout
        var names = mutableListOf<String>()
        list.forEach({
            names.add(it.key.substring(3))
        })
        val adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item,
                names
            )
        }

        _binding.spinnerCurrency.adapter = adapter
/*        ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item, names
            ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            _binding.spinnerCurrency.adapter = adapter
        }*/
    }

    private fun setupObserver() {
        Log.d(TAG, " currency-app setupObserver")
        currencyViewModel.data.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        updateList(list)
                        setupSpinner(list)
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

    private fun updateList(currencies: MutableMap<String, Double>) {
        _binding.recyclerView.apply {
            if(visibility != View.VISIBLE) visibility = View.VISIBLE
        }
        currencyList.clear()
        currencies.forEach{
            currencyList.add(CurrencyEntity(0, it.key, it.value))
        }
        currencyAdapter.notifyDataSetChanged()
    }

    private fun createPeriodicWorkRequest() {
        val currencyDataReceiver = PeriodicWorkRequestBuilder<ServerDataReceiver>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("currencyDataWork")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "preodicCurrencyData",
            ExistingPeriodicWorkPolicy.KEEP,
            currencyDataReceiver
        )
        observeWork(currencyDataReceiver.id)
    }

    private fun observeWork(id: UUID) {
        // 1
        workManager.getWorkInfoByIdLiveData(id)
            .observe(viewLifecycleOwner, { info ->
                // 2
                if (info != null && info.state.isFinished) {
                    //hideLottieAnimation()
                    Toast.makeText(requireContext(), "Got data by workmanager", Toast.LENGTH_LONG)
                        .show()

                }
            })
    }
}