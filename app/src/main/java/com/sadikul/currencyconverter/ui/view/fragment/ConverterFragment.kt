package com.sadikul.currencyconverter.ui.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
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
import com.sadikul.currencyconverter.utils.Constants
import com.sadikul.currencyconverter.utils.Status
import com.sadikul.currencyconverter.utils.Utill
import com.sadikul.currencyconverter.worker.CurrencyDataWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class ConverterFragment : Fragment() {
    private val currencyViewModel: CurrencyViewModel by viewModels()
    private val currencyList = mutableListOf<CurrencyEntity>()
    private lateinit var _binding: FragmentConverterBinding
    private val TAG = ConverterFragment::class.java.simpleName
    private lateinit var currencyAdapter: CurrencyAdapter
    @Inject
    lateinit var workManager: WorkManager
    @Inject
    lateinit var currencyWorkRequest: PeriodicWorkRequest
    private var selectedCurrency = "USD"

    private var isSpinnerHasSet: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Get currency value in every 30 mins
        createPeriodicWorkRequest()
        _binding = FragmentConverterBinding.bind(view)
        _binding.textfieldCurrencyInput.text = Editable.Factory.getInstance().newEditable("1")
        Utill.hideKeyboard(requireContext(),view)
        observeTextChange()
        setupObserver()
        setupRecyclerView()
        currencyViewModel.getData(selectedCurrency, "1")
    }

    private fun observeTextChange() {
        _binding.textfieldCurrencyInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! > 0) {
                    currencyViewModel.getData(selectedCurrency, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun setupSpinner(list: MutableMap<String, Double>) {
        Log.d(TAG, " currency-app setupSpinner Setting up spinner")
        isSpinnerHasSet = true
        var names = mutableListOf<String>()
        list.forEach({
            names.add(it.key)
        })
        val adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item,
                names
            )
        }

        _binding.spinnerCurrency.adapter = adapter
        _binding.spinnerCurrency.setSelection(names.indexOf("USD"))
        _binding.spinnerCurrency.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                //if(position != names.indexOf("USD")){
                selectedCurrency = names.get(position)
                currencyViewModel.getData(selectedCurrency, _binding.textfieldCurrencyInput.text.toString())
                //}
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

        _binding.spinnerCurrency.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                view?.let{
                    Utill.hideKeyboard(requireContext(),it)
                }
                return false
            }

        })
    }

    private fun setupObserver() {
        Log.d(TAG, " currency-app setupObserver")
        currencyViewModel.data.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    hideLoader(true)
                    it.data?.let { list ->
                        updateList(list)
                        if(!isSpinnerHasSet) {
                            setupSpinner(list)
                        }
                    }
                }

                Status.LOADING -> {
                    showLoader()
                    //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "currency-app loading started")
                }

                Status.ERROR -> {
                    hideLoader(false)
                    Toast.makeText(requireContext(), it?.message, Toast.LENGTH_LONG)
                    .show()
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
        workManager.enqueueUniquePeriodicWork(
            Constants.PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            currencyWorkRequest
        )
        observeWork(currencyWorkRequest.id)
    }

    private fun observeWork(id: UUID) {
        workManager.getWorkInfoByIdLiveData(id)
            .observe(viewLifecycleOwner, { info ->
                if (info != null) {
                    Log.e(TAG,"Currency data is updated")
                   // Toast.makeText(requireContext(), "Currency data is updated", Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun showLoader() = _binding?.apply {
        progrssBar.visibility = View.VISIBLE
        //_binding.recyclerView.visibility = View.GONE
    }

    private fun hideLoader(showRecyclerview: Boolean) = _binding?.apply {
        progrssBar.visibility = View.GONE
        //_binding.recyclerView.visibility = if (showRecyclerview) View.VISIBLE else View.GONE
    }
}