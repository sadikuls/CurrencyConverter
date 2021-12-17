package com.sadikul.currencyconverter.ui.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.databinding.ItemGridLayoutBinding
import com.sadikul.currencyconverter.utils.Utill


class CurrencyAdapter(
    var galleryItemList: MutableList<CurrencyEntity>,
    val onClick: (CurrencyEntity?) -> Unit
) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {
    init {
        Log.d("CurrencyAdapter", "called CurrencyAdapter")
    }

    inner class CurrencyViewHolder(binding: ItemGridLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val _binding: ItemGridLayoutBinding = binding

        init {
            itemView.setOnClickListener {
                onClick(galleryItemList[adapterPosition])
            }
        }

        fun bind(currencyItem: CurrencyEntity) {
            _binding.apply {
                tvCurrency.text = currencyItem.currency
                tvCurrencyValue.text = currencyItem.value?.let {
                    Utill.roundOffDecimal(it)
                }.toString()
                //Log.d("GalleryAdapter",imageUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val _binding =
            ItemGridLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(_binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(galleryItemList[position])
    }

    override fun getItemCount(): Int = galleryItemList.size

}