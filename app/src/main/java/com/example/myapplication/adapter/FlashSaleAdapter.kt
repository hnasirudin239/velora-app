package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemFlashSaleBinding

class FlashSaleAdapter(
    private val products: List<Triple<String, String, String>>
) : BaseAdapter() {

    override fun getCount(): Int = products.size

    override fun getItem(position: Int): Any = products[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemFlashSaleBinding
        val view: View

        if (convertView == null) {
            binding = ItemFlashSaleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemFlashSaleBinding
        }

        val product = products[position]
        binding.tvFlashPrice.text = product.first
        binding.tvFlashOriginalPrice.text = product.second
        binding.tvDiscountBadge.text = product.third

        // Progress bar random (simulasi)
        val progress = (20..90).random()
        
        // ✅ Perbaiki: Cast parent ke View untuk mendapatkan width
        binding.vProgressFill.post {
            val parentView = binding.vProgressFill.parent as? View
            parentView?.let {
                val parentWidth = it.width
                if (parentWidth > 0) {
                    val params = binding.vProgressFill.layoutParams
                    params.width = parentWidth * progress / 100
                    binding.vProgressFill.layoutParams = params
                }
            }
        }

        return view
    }
}