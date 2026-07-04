package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemOrderSummaryBinding
import com.example.myapplication.model.CartItem

class OrderSummaryAdapter(
    private val items: List<CartItem>
) : RecyclerView.Adapter<OrderSummaryAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ItemOrderSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvOrderName.text = item.productName
            tvOrderQuantity.text = "x ${item.quantity}"
            tvOrderPrice.text = "Rp ${String.format("%,.0f", item.productPrice * item.quantity)}"
            ivOrderImage.setImageResource(item.productImageRes)
        }
    }

    override fun getItemCount(): Int = items.size
}