package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.OrderItem

class OrderDetailProductAdapter(
    private val items: List<OrderItem>
) : RecyclerView.Adapter<OrderDetailProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.iv_product_image)
        val tvName: TextView = itemView.findViewById(R.id.tv_product_name)
        val tvQty: TextView = itemView.findViewById(R.id.tv_product_qty)
        val tvSubtotal: TextView = itemView.findViewById(R.id.tv_product_subtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        holder.ivImage.setImageResource(item.imageRes)
        holder.tvName.text = item.productName
        holder.tvQty.text = "x ${item.quantity}"
        holder.tvSubtotal.text = "Rp ${String.format("%,.0f", item.price * item.quantity)}"
    }

    override fun getItemCount(): Int = items.size
}