package com.example.myapplication.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.CarouselAdapter
import com.example.myapplication.adapter.FlashSaleAdapter
import com.example.myapplication.adapter.ProductAdapterHome
import com.example.myapplication.data.DummyDataSource
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.Product

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val promoImages = listOf(
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background
    )

    private val categoryNames = listOf(
        "Pulsa", "Paket Data", "Listrik", "Hewan",
        "Keuangan", "Handphone", "Elektronik", "Komputer"
    )
    private val categoryIcons = listOf(
        R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_shopping_cart,
        R.drawable.ic_favorite, R.drawable.ic_profile, R.drawable.ic_home,
        R.drawable.ic_search, R.drawable.ic_shopping_cart
    )

    private val flashSaleProducts = listOf(
        Triple("Rp 150.000", "Rp 300.000", "50%"),
        Triple("Rp 210.000", "Rp 300.000", "30%"),
        Triple("Rp 595.000", "Rp 700.000", "15%"),
        Triple("Rp 125.000", "Rp 250.000", "50%")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarousel()
        setupCategories()
        setupFlashSale()
        setupProducts()
        setupListeners()
        startFlashSaleTimer()
    }

    private fun setupCarousel() {
        val adapter = CarouselAdapter(promoImages)
        binding.viewPager.adapter = adapter

        setupDotIndicator(adapter.itemCount)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDotIndicator(position)
            }
        })
    }

    private fun setupDotIndicator(count: Int) {
        binding.dotContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = TextView(requireContext()).apply {
                text = "●"
                textSize = 18f
                setTextColor(
                    if (i == 0) {
                        android.graphics.Color.parseColor("#1A73E8")
                    } else {
                        android.graphics.Color.parseColor("#D1D5DB")
                    }
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(4, 0, 4, 0)
                }
            }
            binding.dotContainer.addView(dot)
        }
    }

    private fun updateDotIndicator(position: Int) {
        for (i in 0 until binding.dotContainer.childCount) {
            val dot = binding.dotContainer.getChildAt(i) as TextView
            dot.setTextColor(
                if (i == position) {
                    android.graphics.Color.parseColor("#1A73E8")
                } else {
                    android.graphics.Color.parseColor("#D1D5DB")
                }
            )
        }
    }

    private fun setupCategories() {
        val gridView = binding.gvCategories
        val items = categoryNames.mapIndexed { index, name ->
            mapOf(
                "name" to name,
                "icon" to categoryIcons[index]
            )
        }

        val adapter = object : SimpleAdapter(
            requireContext(),
            items,
            R.layout.item_category_home,
            arrayOf("name", "icon"),
            intArrayOf(R.id.tv_category_name, R.id.iv_category_icon)
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val iconView = view.findViewById<ImageView>(R.id.iv_category_icon)
                iconView.setImageResource(categoryIcons[position])
                return view
            }
        }

        gridView.adapter = adapter
    }

    // ✅ Tambahan: Setup Flash Sale
    private fun setupFlashSale() {
        val adapter = FlashSaleAdapter(flashSaleProducts)
        binding.flashSaleContainer.removeAllViews()
        
        // ✅ Gunakan adapter.count (bukan itemCount)
        for (i in 0 until adapter.count) {
            val view = adapter.getView(i, null, binding.flashSaleContainer)
            binding.flashSaleContainer.addView(view)
        }
    }

    private fun setupProducts() {
        val products = DummyDataSource.getProducts()
        val adapter = ProductAdapterHome(
            products
        ) { product ->
            navigateToDetail(product)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
    }

    // ✅ Timer Countdown Flash Sale
    private fun startFlashSaleTimer() {
        var hours = 24
        var minutes = 0
        var seconds = 0

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (!isAdded || _binding == null) return

                binding.tvTimerHours.text = String.format("%02d", hours)
                binding.tvTimerMinutes.text = String.format("%02d", minutes)
                binding.tvTimerSeconds.text = String.format("%02d", seconds)

                seconds--
                if (seconds < 0) {
                    seconds = 59
                    minutes--
                    if (minutes < 0) {
                        minutes = 59
                        hours--
                        if (hours < 0) {
                            hours = 0
                            minutes = 0
                            seconds = 0
                        }
                    }
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun setupListeners() {
        binding.etSearchHome.setOnClickListener {
            (activity as? MainActivity)?.navigateToSearch()
        }

        binding.ivNotification.setOnClickListener {
            // Tampilkan notifikasi
        }

        binding.ivCartHeader.setOnClickListener {
            (activity as? MainActivity)?.navigateToCart()
        }
    }

    private fun navigateToDetail(product: Product) {
        val intent = android.content.Intent(requireContext(), com.example.myapplication.ProductDetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}