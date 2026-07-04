package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.OrderDetailProductAdapter
import com.example.myapplication.databinding.ActivityOrderDetailBinding
import com.example.myapplication.model.OrderDetail
import com.example.myapplication.model.OrderItem
import com.example.myapplication.model.TimelineStep
import com.example.myapplication.PostReviewActivity
import android.content.Intent

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var adapter: OrderDetailProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent (atau gunakan dummy)
        val order = intent.getSerializableExtra("order") as? OrderDetail
        if (order != null) {
            setupView(order)
        } else {
            setupDummyData()
        }

        setupListeners()
    }

    private fun setupView(order: OrderDetail) {
        // Header
        binding.tvOrderId.text = order.id
        binding.tvOrderDate.text = "${order.date}, ${order.time}"
        binding.tvStatus.text = order.statusLabel
        binding.tvStatus.setBackgroundResource(order.statusColor)

        // Timeline
        setupTimeline(order.timeline)

        // Courier
        binding.tvCourierInfo.text = "Kurir: ${order.courier} (No. Resi: ${order.trackingNumber})"

        // Address
        binding.tvAddressName.text = order.addressName
        binding.tvAddressDetail.text = order.addressDetail
        binding.tvAddressPhone.text = order.addressPhone

        // Products
        adapter = OrderDetailProductAdapter(order.items)
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)
        binding.rvOrderItems.adapter = adapter

        // Payment Breakdown
        val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID"))
        binding.tvSubtotalLabel.text = "Total Barang (${order.items.size} item)"
        binding.tvSubtotalValue.text = "Rp ${formatter.format(order.subtotal)}"
        binding.tvShippingValue.text = "Rp ${formatter.format(order.shippingCost)}"
        binding.tvPaymentMethod.text = order.paymentMethod
        binding.ivPaymentIcon.setImageResource(order.paymentMethodIcon)
        binding.tvTotalValue.text = "Rp ${formatter.format(order.total)}"

        // Bottom buttons based on status
        updateBottomButtons(order.status)
    }

    private fun setupDummyData() {
        val dummyOrder = OrderDetail(
            id = "#INV-20260629-001",
            date = "29 Jun 2026",
            time = "14:30",
            status = "shipped",
            statusLabel = "Dalam Pengiriman",
            statusColor = R.drawable.bg_status_shipped,
            items = listOf(
                OrderItem("Sepatu Lari Aerox", 1, 450000.0, R.drawable.ic_launcher_foreground),
                OrderItem("Jam Tangan Smart X", 1, 1250000.0, R.drawable.ic_launcher_foreground)
            ),
            subtotal = 1700000.0,
            shippingCost = 12000.0,
            paymentMethod = "Transfer Bank (BCA)",
            paymentMethodIcon = R.drawable.ic_payment,
            total = 1742000.0,
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
        setupView(dummyOrder)
    }

    private fun setupTimeline(timeline: List<TimelineStep>) {
        val container = binding.timelineContainer
        container.removeAllViews()

        timeline.forEachIndexed { index, step ->
            val itemView = layoutInflater.inflate(R.layout.item_timeline_step, container, false)
            val circle = itemView.findViewById<View>(R.id.timeline_circle)
            val label = itemView.findViewById<TextView>(R.id.timeline_label)

            if (step.isCompleted) {
                circle.setBackgroundResource(R.drawable.bg_timeline_completed)
            } else if (step.isActive) {
                circle.setBackgroundResource(R.drawable.bg_timeline_active)
            } else {
                circle.setBackgroundResource(R.drawable.bg_timeline_inactive)
            }

            label.text = step.label
            label.setTextColor(
                if (step.isCompleted || step.isActive) {
                    resources.getColor(R.color.text_primary)
                } else {
                    resources.getColor(R.color.gray_border)
                }
            )

            container.addView(itemView)

            // Add divider between steps (except last)
            if (index < timeline.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        2,
                        1f
                    )
                    setBackgroundColor(resources.getColor(if (step.isCompleted) R.color.blue_primary else R.color.gray_light))
                }
                container.addView(divider)
            }
        }
    }

    private fun updateBottomButtons(status: String) {
        when (status) {
            "pending" -> {
                binding.btnContactSeller.text = "Hubungi CS"
                binding.btnTrackOrder.text = "Bayar Sekarang"
                binding.btnTrackOrder.setBackgroundColor(resources.getColor(R.color.blue_primary))
            }
            "shipped" -> {
                binding.btnContactSeller.text = "Hubungi CS"
                binding.btnTrackOrder.text = "Lacak Pengiriman"
                binding.btnTrackOrder.setBackgroundColor(resources.getColor(R.color.blue_primary))
            }
            "done" -> {
                binding.btnContactSeller.text = "Beli Lagi"
                binding.btnTrackOrder.text = "Ulas Produk"
                binding.btnTrackOrder.setBackgroundColor(resources.getColor(R.color.blue_primary))
                binding.btnTrackOrder.visibility = View.GONE
                binding.btnReview.visibility = View.VISIBLE
            }
            else -> {
                binding.btnContactSeller.text = "Hubungi CS"
                binding.btnTrackOrder.text = "Lihat Detail"
                binding.btnTrackOrder.setBackgroundColor(resources.getColor(R.color.blue_primary))
            }
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.btnContactSeller.setOnClickListener {
            // Aksi hubungi penjual

        }

        binding.btnTrackOrder.setOnClickListener {
            // Aksi sesuai tombol
            
        }

        binding.btnReview.setOnClickListener{
            startActivity(Intent(this, PostReviewActivity::class.java))
        }
    }
}