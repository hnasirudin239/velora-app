package com.example.myapplication

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.VoucherAdapter
import com.example.myapplication.databinding.ActivityMyVoucherBinding
import com.example.myapplication.model.Voucher
import android.content.ClipboardManager
import android.content.Context

class MyVoucherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyVoucherBinding
    private lateinit var adapter: VoucherAdapter
    private var allVouchers = listOf<Voucher>()
    private var filteredVouchers = listOf<Voucher>()
    private var currentTab = "Tersedia"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDummyData()
        setupRecyclerView()
        setupTabs()
        setupListeners()
        filterVouchers("Tersedia")
    }

    private fun setupDummyData() {
        allVouchers = listOf(
            Voucher(1, "money", "Rp 50K", "Diskon Belanja", "Min. 250K",
                "Voucher Belanja Akhir Bulan", "NEURO50K", "15 Jul 2026", "active"),
            Voucher(2, "percent", "20%", "Potongan", "Maks. 50K",
                "Diskon Spesial Pengguna Baru", "NEURO20", "10 Jul 2026", "active"),
            Voucher(3, "shipping", "Gratis", "Ongkir", "Min. 100K",
                "Bebas Ongkir All Area", "FREESHIP", "20 Jun 2026", "expired")
        )
    }

    private fun setupRecyclerView() {
        filteredVouchers = allVouchers
        adapter = VoucherAdapter(filteredVouchers) { voucher ->
            // Copy kode
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("Voucher Code", voucher.code)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "✅ Kode ${voucher.code} disalin!", Toast.LENGTH_SHORT).show()
        }
        binding.rvVouchers.layoutManager = LinearLayoutManager(this)
        binding.rvVouchers.adapter = adapter
    }

    private fun setupTabs() {
        val tabs = listOf("Tersedia", "Digunakan", "Kadaluarsa")
        val container = binding.tabContainer
        container.removeAllViews()

        tabs.forEach { tab ->
            val tv = TextView(this).apply {
                text = tab
                setPadding(16, 6, 16, 6)
                textSize = 13f
                val isActive = tab == "Tersedia"
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
                tag = tab

                setOnClickListener {
                    currentTab = tab
                    updateTabUI()
                    filterVouchers(tab)
                }
            }
            container.addView(tv)
        }
    }

    private fun updateTabUI() {
        for (i in 0 until binding.tabContainer.childCount) {
            val child = binding.tabContainer.getChildAt(i) as TextView
            val isActive = child.tag == currentTab
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

    private fun filterVouchers(filter: String) {
        filteredVouchers = when (filter) {
            "Digunakan" -> allVouchers.filter { it.status == "used" }
            "Kadaluarsa" -> allVouchers.filter { it.status == "expired" }
            else -> allVouchers.filter { it.status == "active" }
        }
        adapter = VoucherAdapter(filteredVouchers) { voucher ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("Voucher Code", voucher.code)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "✅ Kode ${voucher.code} disalin!", Toast.LENGTH_SHORT).show()
        }
        binding.rvVouchers.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}