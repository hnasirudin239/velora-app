package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Voucher

class VoucherModalAdapter(
    private val vouchers: List<Voucher>,
    private var selectedPosition: Int,
    private val onItemClick: (Voucher, Int) -> Unit
) : RecyclerView.Adapter<VoucherModalAdapter.VoucherViewHolder>() {

    class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDiscount: TextView = itemView.findViewById(R.id.tv_voucher_discount_modal)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_voucher_title_modal)
        val tvCode: TextView = itemView.findViewById(R.id.tv_voucher_code_modal)
        val tvMinSpend: TextView = itemView.findViewById(R.id.tv_voucher_min_spend_modal)
        val rbSelect: RadioButton = itemView.findViewById(R.id.rb_select_voucher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voucher_modal, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = vouchers[position]
        holder.tvDiscount.text = voucher.discountAmount.replace("Rp ", "").replace("K", "K")
        holder.tvTitle.text = voucher.title
        holder.tvCode.text = voucher.code
        holder.tvMinSpend.text = voucher.minSpend.replace("Min. ", "Min. Belanja ")

        holder.rbSelect.isChecked = (position == selectedPosition)
        holder.rbSelect.setOnClickListener {
            onItemClick(voucher, position)
        }

        holder.itemView.setOnClickListener {
            onItemClick(voucher, position)
        }
    }

    override fun getItemCount(): Int = vouchers.size

    // ✅ Fungsi untuk update selected position tanpa membuat ulang adapter
    fun updateSelectedPosition(newPosition: Int) {
        selectedPosition = newPosition
    }
}