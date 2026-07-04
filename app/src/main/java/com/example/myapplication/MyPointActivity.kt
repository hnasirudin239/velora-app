package com.example.myapplication

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.PointHistoryAdapter
import com.example.myapplication.databinding.ActivityMyPointBinding
import com.example.myapplication.model.PointHistory

class MyPointActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPointBinding
    private lateinit var adapter: PointHistoryAdapter
    private var allHistories = listOf<PointHistory>()
    private var filteredHistories = listOf<PointHistory>()
    private var currentFilter = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDummyData()
        setupRecyclerView()
        setupFilterPills()
        setupListeners()
        applyFilter()
    }

    private fun setupDummyData() {
        allHistories = listOf(
            PointHistory(1, "Review Produk Sepatu Lari", "29 Jun 2026, 14:30", 50, "earn"),
            PointHistory(2, "Penukaran Voucher Belanja", "28 Jun 2026, 09:15", -200, "spend"),
            PointHistory(3, "Bonus Poin Hari Belanja", "25 Jun 2026, 11:02", 150, "earn"),
            PointHistory(4, "Poin akan kadaluarsa", "Berlaku hingga 30 Jun 2026", 0, "expire"),
            PointHistory(5, "Pembelian Produk", "20 Jun 2026, 16:45", 75, "earn"),
            PointHistory(6, "Penukaran Diskon", "15 Jun 2026, 10:30", -100, "spend")
        )

        // Set total points
        val totalPoints = allHistories.filter { it.type != "expire" }.sumOf { it.points }
        binding.tvTotalPoints.text = totalPoints.toString()

        // Set equivalent value (1000 poin = Rp 10.000)
        val equivalent = (totalPoints / 1000.0 * 10000).toInt()
        binding.tvPointsEquivalent.text = "Senilai Rp ${String.format("%,.0f", equivalent.toDouble())} (1.000 Poin = Rp 10.000)"
    }

    private fun setupRecyclerView() {
        filteredHistories = allHistories
        adapter = PointHistoryAdapter(filteredHistories)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun setupFilterPills() {
        val filters = listOf("Semua", "Masuk")
        val container = binding.filterContainer
        container.removeAllViews()

        filters.forEach { filter ->
            val tv = TextView(this).apply {
                text = filter
                setPadding(12, 4, 12, 4)
                textSize = 12f
                val isActive = filter == "Semua"
                setTextColor(
                    if (isActive) {
                        resources.getColor(R.color.blue_primary, theme)
                    } else {
                        resources.getColor(R.color.text_primary, theme)
                    }
                )
                background = if (isActive) {
                    getDrawable(R.drawable.bg_filter_pill_active)
                } else {
                    getDrawable(R.drawable.bg_filter_pill_inactive)
                }
                isClickable = true
                tag = filter

                setOnClickListener {
                    currentFilter = filter
                    updateFilterUI()
                    applyFilter()
                }
            }
            container.addView(tv)
        }
    }

    private fun updateFilterUI() {
        for (i in 0 until binding.filterContainer.childCount) {
            val child = binding.filterContainer.getChildAt(i) as TextView
            val isActive = child.tag == currentFilter
            child.setTextColor(
                if (isActive) {
                    resources.getColor(R.color.blue_primary, theme)
                } else {
                    resources.getColor(R.color.text_primary, theme)
                }
            )
            child.background = if (isActive) {
                getDrawable(R.drawable.bg_filter_pill_active)
            } else {
                getDrawable(R.drawable.bg_filter_pill_inactive)
            }
        }
    }

    private fun applyFilter() {
        filteredHistories = when (currentFilter) {
            "Masuk" -> allHistories.filter { it.type == "earn" }
            else -> allHistories
        }
        adapter = PointHistoryAdapter(filteredHistories)
        binding.rvHistory.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}