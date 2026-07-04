package com.example.myapplication.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductAdapterHome
import com.example.myapplication.data.DummyDataSource
import com.example.myapplication.databinding.FragmentSearchBinding
import com.example.myapplication.model.Product

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var allProducts = listOf<Product>()
    private var filteredProducts = listOf<Product>()
    private lateinit var adapter: ProductAdapterHome
    private var currentFilter = "Semua"
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allProducts = DummyDataSource.getProducts()
        filteredProducts = allProducts

        setupRecyclerView()
        setupSearchBar()
        setupFilterButtons()
        setupClearFilterButton()
        updateResults()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapterHome(
            filteredProducts
        ) { product ->
            navigateToDetail(product)
        }
        binding.rvSearchResults.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = {
                    performSearch(s.toString())
                }
                searchRunnable?.let { searchHandler.postDelayed(it, 300) }
            }
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.etSearch.text.toString())
                true
            } else {
                false
            }
        }

        binding.btnClearSearch.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
            }
            binding.etSearch.text.clear()
            binding.btnClearSearch.visibility = View.GONE
            performSearch("")
        }
    }

    private fun setupFilterButtons() {
        val filters = listOf("Semua", "Elektronik", "Fashion", "Rumah")

        binding.filterContainer.removeAllViews()

        filters.forEach { filter ->
            val tv = TextView(requireContext()).apply {
                text = filter
                setPadding(20, 8, 20, 8)
                textSize = 14f
                tag = filter
                isClickable = true
                isFocusable = true

                val isSelected = (filter == "Semua")

                background = GradientDrawable().apply {
                    if (isSelected) {
                        setColor(ContextCompat.getColor(context, R.color.blue_primary))
                        cornerRadius = 20f
                    } else {
                        setColor(ContextCompat.getColor(context, R.color.gray_light))
                        cornerRadius = 20f
                        setStroke(1, ContextCompat.getColor(context, R.color.gray_border))
                    }
                }

                setTextColor(
                    if (isSelected) {
                        ContextCompat.getColor(context, R.color.white)
                    } else {
                        ContextCompat.getColor(context, R.color.text_primary)
                    }
                )

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 8, 0)
                }

                setOnClickListener { view ->
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)

                    // ✅ Cast child ke TextView
                    for (i in 0 until binding.filterContainer.childCount) {
                        val child = binding.filterContainer.getChildAt(i) as TextView
                        val isAll = child.tag == "Semua"

                        child.background = GradientDrawable().apply {
                            if (isAll) {
                                setColor(ContextCompat.getColor(context, R.color.blue_primary))
                                cornerRadius = 20f
                            } else {
                                setColor(ContextCompat.getColor(context, R.color.gray_light))
                                cornerRadius = 20f
                                setStroke(1, ContextCompat.getColor(context, R.color.gray_border))
                            }
                        }

                        child.setTextColor(
                            if (isAll) {
                                ContextCompat.getColor(context, R.color.white)
                            } else {
                                ContextCompat.getColor(context, R.color.text_primary)
                            }
                        )
                    }

                    // Aktifkan yang dipilih
                    view.background = GradientDrawable().apply {
                        setColor(ContextCompat.getColor(context, R.color.blue_primary))
                        cornerRadius = 20f
                    }
                    (view as TextView).setTextColor(ContextCompat.getColor(context, R.color.white))

                    currentFilter = filter
                    performSearch(binding.etSearch.text.toString())
                }
            }
            binding.filterContainer.addView(tv)
        }
    }

    private fun setupClearFilterButton() {
        binding.btnClearFilter.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }

            binding.etSearch.text.clear()
            binding.btnClearSearch.visibility = View.GONE
            resetFilters()
            filteredProducts = allProducts
            updateResults()
        }
    }

    private fun resetFilters() {
        // ✅ Cast child ke TextView
        for (i in 0 until binding.filterContainer.childCount) {
            val child = binding.filterContainer.getChildAt(i) as TextView
            val isAll = child.tag == "Semua"

            child.background = GradientDrawable().apply {
                if (isAll) {
                    setColor(ContextCompat.getColor(requireContext(), R.color.blue_primary))
                    cornerRadius = 20f
                } else {
                    setColor(ContextCompat.getColor(requireContext(), R.color.gray_light))
                    cornerRadius = 20f
                    setStroke(1, ContextCompat.getColor(requireContext(), R.color.gray_border))
                }
            }

            child.setTextColor(
                if (isAll) {
                    ContextCompat.getColor(requireContext(), R.color.white)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.text_primary)
                }
            )
        }
        currentFilter = "Semua"
    }

    private fun performSearch(query: String) {
        binding.progressBar.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            var results = if (query.isEmpty()) {
                allProducts
            } else {
                allProducts.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.description.contains(query, ignoreCase = true)
                }
            }

            when (currentFilter) {
                "Elektronik" -> {
                    results = results.filter { it.id in listOf(1, 2) }
                }
                "Fashion" -> {
                    results = results.filter { it.id in listOf(3) }
                }
                "Rumah" -> {
                    results = results.filter { it.id in listOf(4) }
                }
                else -> { /* Semua produk */ }
            }

            filteredProducts = results
            updateResults()
            binding.progressBar.visibility = View.GONE

        }, 300)
    }

    private fun updateResults() {
        adapter = ProductAdapterHome(
            filteredProducts
        ) { product ->
            navigateToDetail(product)
        }
        binding.rvSearchResults.adapter = adapter
        binding.tvResultCount.text = "Menampilkan ${filteredProducts.size} produk"

        if (filteredProducts.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.rvSearchResults.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.rvSearchResults.visibility = View.VISIBLE
        }

        binding.rvSearchResults.animate().alpha(0f).setDuration(0).start()
        binding.rvSearchResults.animate().alpha(1f).setDuration(300).start()
    }

    private fun navigateToDetail(product: Product) {
        val intent = android.content.Intent(requireContext(), com.example.myapplication.ProductDetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }
}