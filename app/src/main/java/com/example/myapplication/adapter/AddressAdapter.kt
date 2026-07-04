package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Address
import android.content.Intent
import com.example.myapplication.AddressFormActivity

class AddressAdapter(
    private val addresses: List<Address>,
    private val onEdit: (Address) -> Unit,
    private val onDelete: (Address) -> Unit,
    private val onItemClick: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_address_name)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_address_phone)
        val tvBadge: TextView = itemView.findViewById(R.id.tv_badge)
        val tvDetail: TextView = itemView.findViewById(R.id.tv_address_detail)
        val tvEdit: TextView = itemView.findViewById(R.id.tv_edit)
        val tvDelete: TextView = itemView.findViewById(R.id.tv_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        holder.tvName.text = address.name
        holder.tvPhone.text = address.phone
        holder.tvDetail.text = address.address

        if (address.isDefault) {
            holder.tvBadge.visibility = android.view.View.VISIBLE
            holder.tvBadge.text = "⭐ Utama"
        } else {
            holder.tvBadge.visibility = android.view.View.GONE
        }

        holder.tvEdit.setOnClickListener { 
            val intent = Intent(holder.itemView.context, AddressFormActivity::class.java)
            intent.putExtra("address", address)
            holder.itemView.context.startActivity(intent) 
        }
        holder.tvDelete.setOnClickListener { onDelete(address) }
        holder.itemView.setOnClickListener { onItemClick(address) }
    }

    override fun getItemCount(): Int = addresses.size
}