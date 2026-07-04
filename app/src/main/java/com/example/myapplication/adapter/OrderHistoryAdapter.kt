package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.OrderDetailActivity
import com.example.myapplication.model.OrderDetail
import com.example.myapplication.model.OrderHistory
import com.example.myapplication.model.OrderItem
import com.example.myapplication.model.TimelineStep
import java.text.NumberFormat
import java.util.Locale
import com.example.myapplication.PostReviewActivity

class OrderHistoryAdapter(
    private val onActionClick: (OrderHistory, String) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    private var orders = listOf<OrderHistory>()

    fun submitList(newOrders: List<OrderHistory>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvOrderId: TextView = itemView.findViewById(R.id.tv_order_id)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tv_order_date)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        private val llProducts: LinearLayout = itemView.findViewById(R.id.ll_products)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tv_total_price)
        private val llActions: LinearLayout = itemView.findViewById(R.id.ll_actions)

        fun bind(order: OrderHistory) {
            tvOrderId.text = order.id
            tvOrderDate.text = "${order.date}, ${order.time}"

            // Status
            tvStatus.text = order.statusLabel
            tvStatus.setBackgroundResource(
                when (order.status) {
                    "pending" -> R.drawable.bg_status_pending
                    "shipped" -> R.drawable.bg_status_shipped
                    "done" -> R.drawable.bg_status_done
                    "cancelled" -> R.drawable.bg_status_cancelled
                    else -> R.drawable.bg_status_pending
                }
            )

            // Products
            llProducts.removeAllViews()
            order.items.forEach { item ->
                val productView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_order_product_mini, llProducts, false)
                val ivImage = productView.findViewById<ImageView>(R.id.iv_product_mini)
                val tvName = productView.findViewById<TextView>(R.id.tv_product_name_mini)
                val tvQty = productView.findViewById<TextView>(R.id.tv_product_qty_mini)

                ivImage.setImageResource(item.imageRes)
                tvName.text = item.productName
                tvQty.text = "x${item.quantity}"
                llProducts.addView(productView)
            }

            // Total
            val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
            tvTotalPrice.text = "Rp ${formatter.format(order.totalPrice)}"

            // Actions
            llActions.removeAllViews()
            when (order.status) {
                "pending" -> {
                    // ✅ Tombol "Lihat Detail" → navigateToDetail
                    addActionButton("Lihat Detail", false) { navigateToDetail(order) }
                    addActionButton("Bayar Sekarang", true) { onActionClick(order, "pay") }
                }
                "shipped" -> {
                    addActionButton("Lacak Paket", true) { onActionClick(order, "track") }
                    // ✅ Tombol "Lihat Detail" → navigateToDetail
                    addActionButton("Lihat Detail", false) { navigateToDetail(order) }
                }
                "done" -> {
                    addActionButton("Beli Lagi", false) { onActionClick(order, "repeat") }
                    addActionButton("Ulas", false) { navigateToReview() }
                }
                else -> {
                    // ✅ Tombol "Lihat Detail" → navigateToDetail
                    addActionButton("Lihat Detail", false) { navigateToDetail(order) }
                }
            }

            // ✅ Klik item → navigateToDetail
            itemView.setOnClickListener { navigateToDetail(order) }
        }

        private fun addActionButton(text: String, isPrimary: Boolean, onClick: () -> Unit) {
            val button = Button(itemView.context).apply {
                this.text = text
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 8
                }
                setPadding(16, 8, 16, 8)
                textSize = 12f
                setTextColor(if (isPrimary) 0xFFFFFFFF.toInt() else 0xFF4B5563.toInt())
                background = if (isPrimary) {
                    itemView.context.getDrawable(R.drawable.bg_button_primary)
                } else {
                    itemView.context.getDrawable(R.drawable.bg_button_outline)
                }
                setOnClickListener { onClick() }
            }
            llActions.addView(button)
        }

        // ✅ Fungsi navigasi ke OrderDetailActivity
        private fun navigateToDetail(order: OrderHistory) {
            val context = itemView.context
            val detail = OrderDetail(
                id = order.id,
                date = order.date,
                time = order.time,
                status = order.status,
                statusLabel = order.statusLabel,
                statusColor = when (order.status) {
                    "pending" -> R.drawable.bg_status_pending
                    "shipped" -> R.drawable.bg_status_shipped
                    "done" -> R.drawable.bg_status_done
                    else -> R.drawable.bg_status_pending
                },
                items = order.items,
                subtotal = order.totalPrice,
                shippingCost = 12000.0,
                paymentMethod = "Transfer Bank (BCA)",
                paymentMethodIcon = R.drawable.ic_payment,
                total = order.totalPrice + 12000.0,
                addressName = "Andi Saputra",
                addressDetail = "Jl. Merdeka Raya No. 45, RT 05 RW 02, Kel. Menteng, Kec. Menteng\nJakarta Pusat, DKI Jakarta 10310",
                addressPhone = "+62 812-3456-7890",
                courier = "JNE Reguler",
                trackingNumber = "JNE1234567890",
                timeline = listOf(
                    TimelineStep("Dipesan", true, false),
                    TimelineStep("Dikemas", true, false),
                    TimelineStep("Dikirim", true, true),
                    TimelineStep("Selesai", false, false)
                )
            )
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("order", detail)
            context.startActivity(intent)
        }

        private fun navigateToReview(){
            val context = itemView.context
            val intent = Intent(context, PostReviewActivity::class.java)
            context.startActivity(intent)
        }
    }
}