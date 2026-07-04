package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityOrderHistoryBinding
import com.example.myapplication.model.OrderHistory
import com.example.myapplication.model.OrderItem
import com.example.myapplication.adapter.OrderHistoryAdapter
import com.example.myapplication.PostReviewActivity

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var adapter: OrderHistoryAdapter
    private var currentFilter = "Semua"
    private val allOrders = generateDummyOrders()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupTabs()
        setupListeners()
        filterOrders("Semua")
    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter(
            onActionClick = { order, action ->
                when (action) {
                    "pay" -> Toast.makeText(this, "Bayar ${order.id}", Toast.LENGTH_SHORT).show()
                    "track" -> Toast.makeText(this, "Lacak ${order.id}", Toast.LENGTH_SHORT).show()
                    "repeat" -> Toast.makeText(this, "Beli Lagi", Toast.LENGTH_SHORT).show()
                    "review" -> Toast.makeText(this, "Ulas Produk", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "Detail ${order.id}", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }

    private fun setupTabs() {
        val tabs = listOf("Semua", "Belum Bayar", "Dikirim", "Selesai", "Dibatalkan")
        val container = binding.tabContainer
        container.removeAllViews()

        tabs.forEach { tab ->
            val tv = TextView(this).apply {
                text = tab
                setPadding(16, 6, 16, 6)
                textSize = 13f
                setTextColor(if (tab == "Semua") 0xFF1A73E8.toInt() else 0xFF4B5563.toInt())
                background = if (tab == "Semua") {
                    getDrawable(R.drawable.bg_tab_active)
                } else {
                    getDrawable(R.drawable.bg_tab_inactive)
                }
                isClickable = true
                setOnClickListener {
                    filterOrders(tab)
                    updateTabSelection(tabs, this)
                }
            }
            container.addView(tv)
        }
    }

    private fun updateTabSelection(tabs: List<String>, selectedView: TextView) {
        for (i in 0 until binding.tabContainer.childCount) {
            val child = binding.tabContainer.getChildAt(i) as TextView
            child.setTextColor(if (child == selectedView) 0xFF1A73E8.toInt() else 0xFF4B5563.toInt())
            child.background = if (child == selectedView) {
                getDrawable(R.drawable.bg_tab_active)
            } else {
                getDrawable(R.drawable.bg_tab_inactive)
            }
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }
        binding.btnShopNow.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun filterOrders(filter: String) {
        val filtered = when (filter) {
            "Belum Bayar" -> allOrders.filter { it.status == "pending" }
            "Dikirim" -> allOrders.filter { it.status == "shipped" }
            "Selesai" -> allOrders.filter { it.status == "done" }
            "Dibatalkan" -> allOrders.filter { it.status == "cancelled" }
            else -> allOrders
        }
        adapter.submitList(filtered)

        if (filtered.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.rvOrders.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
        }
    }

    private fun generateDummyOrders(): List<OrderHistory> {
        return listOf(
            OrderHistory(
                id = "#INV-20260629-001",
                date = "29 Jun 2026",
                time = "14:30",
                status = "pending",
                statusLabel = "Menunggu Pembayaran",
                items = listOf(
                    OrderItem("Sepatu Lari Aerox", 1, 450000.0, R.drawable.ic_launcher_foreground),
                    OrderItem("Jam Tangan Smart X", 1, 1250000.0, R.drawable.ic_launcher_foreground)
                ),
                totalPrice = 1742000.0
            ),
            OrderHistory(
                id = "#INV-20260628-002",
                date = "28 Jun 2026",
                time = "09:15",
                status = "shipped",
                statusLabel = "Dalam Pengiriman",
                items = listOf(
                    OrderItem("Headphone Bass Pro", 1, 750000.0, R.drawable.ic_launcher_foreground)
                ),
                totalPrice = 750000.0
            ),
            OrderHistory(
                id = "#INV-20260620-003",
                date = "20 Jun 2026",
                time = "11:45",
                status = "done",
                statusLabel = "Selesai",
                items = listOf(
                    OrderItem("Tas Ransel Anti Air", 1, 320000.0, R.drawable.ic_launcher_foreground)
                ),
                totalPrice = 320000.0
            )
        )
    }
}