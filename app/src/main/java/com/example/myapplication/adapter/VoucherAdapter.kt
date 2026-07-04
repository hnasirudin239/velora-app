package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Voucher

class VoucherAdapter(
    private val vouchers: List<Voucher>,
    private val onCopyClick: (Voucher) -> Unit
) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llLeft: LinearLayout = itemView.findViewById(R.id.ll_left)
        val tvDiscountAmount: TextView = itemView.findViewById(R.id.tv_discount_amount)
        val tvDiscountLabel: TextView = itemView.findViewById(R.id.tv_discount_label)
        val tvMinSpend: TextView = itemView.findViewById(R.id.tv_min_spend)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvCode: TextView = itemView.findViewById(R.id.tv_code)
        val tvCopy: TextView = itemView.findViewById(R.id.tv_copy)
        val tvExpiry: TextView = itemView.findViewById(R.id.tv_expiry)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voucher, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = vouchers[position]

        // Set background kiri berdasarkan tipe
        when (voucher.type) {
            "money" -> holder.llLeft.setBackgroundResource(R.drawable.bg_voucher_money)
            "percent" -> holder.llLeft.setBackgroundResource(R.drawable.bg_voucher_percent)
            "shipping" -> holder.llLeft.setBackgroundResource(R.drawable.bg_voucher_shipping)
            "expired" -> holder.llLeft.setBackgroundResource(R.drawable.bg_voucher_expired)
        }

        holder.tvDiscountAmount.text = voucher.discountAmount
        holder.tvDiscountLabel.text = voucher.discountLabel
        holder.tvMinSpend.text = voucher.minSpend
        holder.tvTitle.text = voucher.title
        holder.tvCode.text = voucher.code

        // Status
        when (voucher.status) {
            "active" -> {
                holder.tvStatus.text = "Tersedia"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active)
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#047857"))
            }
            "used" -> {
                holder.tvStatus.text = "Digunakan"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_used)
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#6B7280"))
            }
            "expired" -> {
                holder.tvStatus.text = "Kadaluarsa"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_expired)
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#DC2626"))
            }
        }

        holder.tvExpiry.text = "Berlaku s/d ${voucher.expiryDate}"

        // Copy button
        holder.tvCopy.setOnClickListener {
            onCopyClick(voucher)
        }

        // Card click
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Voucher: ${voucher.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = vouchers.size
}