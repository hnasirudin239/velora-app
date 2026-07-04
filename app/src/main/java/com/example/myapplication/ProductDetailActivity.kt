package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.adapter.MediaPagerAdapter
import com.example.myapplication.adapter.ProductThumbnailAdapter
import com.example.myapplication.data.database.CartDatabase
import com.example.myapplication.data.repository.CartRepository
import com.example.myapplication.data.repository.WishlistRepository
import com.example.myapplication.databinding.ActivityProductDetailBinding
import com.example.myapplication.model.MediaItem
import com.example.myapplication.model.Product
import com.example.myapplication.model.Review
import com.example.myapplication.model.ReviewMedia  // ✅ Tambahkan ini
import kotlinx.coroutines.launch
import com.example.myapplication.adapter.ReviewAdapter
import android.widget.LinearLayout  // ✅ Tambahkan ini
import android.view.Gravity

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var wishlistRepository: WishlistRepository
    private var product: Product? = null
    private var isInWishlist = false
    private var selectedColor = "Hitam"
    private var selectedSize = "41"
    private lateinit var mediaPagerAdapter: MediaPagerAdapter

    // Di ProductDetailActivity.kt, tambahkan properti:
    private lateinit var reviewAdapter: ReviewAdapter
    private var allReviews = listOf<Review>()
    private var filteredReviews = listOf<Review>()
    private var isFilterMedia = false

    // Data media (gambar + video)
    private val mediaList = listOf(
        MediaItem("image", R.drawable.ic_launcher_foreground),
        MediaItem("image", R.drawable.ic_launcher_background),
        MediaItem("video", null, "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        MediaItem("image", R.drawable.ic_launcher_foreground)
    )

    private val thumbnailImages = listOf(
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_background
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = CartDatabase.getInstance(this)
        cartRepository = CartRepository(database.cartDao())
        wishlistRepository = WishlistRepository(database.wishlistDao())

        product = intent.getSerializableExtra("product") as? Product
        if (product == null) {
            Toast.makeText(this, "Data produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViews()
        setupGallery()
        setupVariants()
        checkWishlistStatus()
        setupListeners()
        setupReviews()
    }

    private fun setupViews() {
        product?.let { p ->
            binding.apply {
                tvProductName.text = p.name
                tvPriceCurrent.text = "Rp ${String.format("%,.0f", p.price)}"
                tvSoldCount.text = "Terjual ${p.soldCount}+"
                rbRating.rating = p.rating
                tvRatingValue.text = p.rating.toString()
                tvDescription.text = p.description

                if (p.soldCount > 3000) {
                    val originalPrice = p.price * 1.2
                    tvPriceOriginal.visibility = View.VISIBLE
                    tvPriceOriginal.text = "Rp ${String.format("%,.0f", originalPrice)}"
                    tvDiscountBadge.visibility = View.VISIBLE
                    val discount = ((1 - (p.price / originalPrice)) * 100).toInt()
                    tvDiscountBadge.text = "-$discount%"
                }
            }
        }
    }

    // ✅ Setup ViewPager dengan MediaPagerAdapter
    private fun setupGallery() {
        // ViewPager untuk gambar + video
        mediaPagerAdapter = MediaPagerAdapter(mediaList)
        binding.viewPagerImages.adapter = mediaPagerAdapter

        // Thumbnail adapter (hanya untuk navigasi)
        val thumbnailAdapter = ProductThumbnailAdapter(thumbnailImages) { position ->
            binding.viewPagerImages.setCurrentItem(position, true)
        }
        binding.rvThumbnails.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvThumbnails.adapter = thumbnailAdapter

        // Update thumbnail dan indicator saat slide
        binding.viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                thumbnailAdapter.notifyDataSetChanged()
                binding.tvGalleryIndicator.text = "${position + 1} / ${mediaList.size}"
            }
        })

        binding.tvGalleryIndicator.text = "1 / ${mediaList.size}"
    }

    private fun setupVariants() {
        // Warna
        val colors = listOf("Hitam", "Putih", "Biru", "Merah")
        val colorContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            
        }
        colors.forEach { color ->
            val tv = TextView(this).apply {
                text = color
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 8)
                textSize = 13f
                setTextColor(if (color == selectedColor) resources.getColor(R.color.blue_primary, theme) else resources.getColor(R.color.text_primary, theme))
                background = if (color == selectedColor) {
                    getDrawable(R.drawable.bg_variant_selected)
                } else {
                    getDrawable(R.drawable.bg_variant_unselected)
                }
                isClickable = true
                setOnClickListener {
                    selectedColor = color
                    updateVariants()
                }
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f).apply {
                    marginEnd = 8
                }
            }
            colorContainer.addView(tv)
        }

        binding.colorOptions.addView(colorContainer)
    

        // Ukuran
        val sizes = listOf("40", "41", "42", "43", "44")
        val sizeContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            weightSum = sizes.size.toFloat()
        }

        sizes.forEach { size ->
            val tv = TextView(this).apply {
                text = size
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 8)
                textSize = 13f
                setTextColor(if (size == selectedSize) resources.getColor(R.color.blue_primary, theme) else resources.getColor(R.color.text_primary, theme))
                background = if (size == selectedSize) {
                    getDrawable(R.drawable.bg_variant_selected)
                } else {
                    getDrawable(R.drawable.bg_variant_unselected)
                }
                isClickable = true
                setOnClickListener {
                    selectedSize = size
                    updateVariants()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = 8
                }
            }
            sizeContainer.addView(tv)
        }

        binding.sizeOptions.addView(sizeContainer)
    }

    private fun updateVariants() {
        // Update warna
        if (binding.colorOptions.childCount > 0) {
            val colorContainer = binding.colorOptions.getChildAt(0) as LinearLayout
            for (i in 0 until colorContainer.childCount) {
                val tv = colorContainer.getChildAt(i) as TextView
                val color = tv.text.toString()
                tv.setTextColor(if (color == selectedColor) resources.getColor(R.color.blue_primary, theme) else resources.getColor(R.color.text_primary, theme))
                tv.background = if (color == selectedColor) {
                    getDrawable(R.drawable.bg_variant_selected)
                } else {
                    getDrawable(R.drawable.bg_variant_unselected)
                }
            }
        }

        // Update ukuran
        if (binding.sizeOptions.childCount > 0) {
            val sizeContainer = binding.sizeOptions.getChildAt(0) as LinearLayout
            for (i in 0 until sizeContainer.childCount) {
                val tv = sizeContainer.getChildAt(i) as TextView
                val size = tv.text.toString()
                tv.setTextColor(if (size == selectedSize) resources.getColor(R.color.blue_primary, theme) else resources.getColor(R.color.text_primary, theme))
                tv.background = if (size == selectedSize) {
                    getDrawable(R.drawable.bg_variant_selected)
                } else {
                    getDrawable(R.drawable.bg_variant_unselected)
                }
            }
        }
    }

    private fun checkWishlistStatus() {
        lifecycleScope.launch {
            product?.let { p ->
                isInWishlist = wishlistRepository.isInWishlist(p.id)
                updateWishlistIcons()
            }
        }
    }

    private fun updateWishlistIcons() {
        val icon = if (isInWishlist) {
            R.drawable.ic_favorite_filled
        } else {
            R.drawable.ic_favorite_border
        }
        binding.btnWishlist.setImageResource(icon)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnShare.setOnClickListener {
            product?.let { p ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Cek produk ini: ${p.name} - Rp ${String.format("%,.0f", p.price)}")
                }
                startActivity(Intent.createChooser(shareIntent, "Bagikan produk"))
            }
        }

        binding.btnWishlistHeader.setOnClickListener { view ->
            animateView(view)
            toggleWishlist()
        }

        binding.btnWishlist.setOnClickListener { view ->
            animateView(view)
            toggleWishlist()
        }

        binding.fabWriteReview.setOnClickListener {
            Toast.makeText(this, "✍️ Tulis Ulasan", Toast.LENGTH_SHORT).show()
            // Buka AddReviewActivity nanti
        }

        binding.btnBuy.setOnClickListener { view ->
            animateView(view)
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }

            product?.let { p ->
                lifecycleScope.launch {
                    try {
                        cartRepository.addToCart(
                            productId = p.id,
                            productName = p.name,
                            productPrice = p.price,
                            productImageRes = p.imageRes
                        )
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "✅ ${p.name} (Warna: $selectedColor, Ukuran: $selectedSize) ditambahkan ke keranjang!",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "❌ Gagal menambahkan ke keranjang",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun toggleWishlist() {
        lifecycleScope.launch {
            product?.let { p ->
                if (isInWishlist) {
                    wishlistRepository.removeFromWishlist(p.id)
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "🗑️ ${p.name} dihapus dari wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    wishlistRepository.addToWishlist(
                        productId = p.id,
                        productName = p.name,
                        productPrice = p.price,
                        productImageRes = p.imageRes,
                        rating = p.rating,
                        soldCount = p.soldCount,
                        description = p.description
                    )
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "❤️ ${p.name} ditambahkan ke wishlist!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                isInWishlist = !isInWishlist
                updateWishlistIcons()
            }
        }
    }

    private fun animateView(view: View) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
        }.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPagerAdapter.pauseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPagerAdapter.releaseAllVideos()
    }

    // Fungsi setupReviews:
    private fun setupReviews() {
        // Data dummy
        allReviews = generateDummyReviews()
        filteredReviews = allReviews

        reviewAdapter = ReviewAdapter(
            reviews = filteredReviews,
            onLikeClick = { review ->
                // Toggle like
                val updated = review.copy(
                    isLiked = !review.isLiked,
                    likeCount = if (review.isLiked) review.likeCount - 1 else review.likeCount + 1
                )
                // Update di list
                val index = allReviews.indexOfFirst { it.id == review.id }
                if (index != -1) {
                    allReviews = allReviews.toMutableList().apply { set(index, updated) }
                    applyFilter()
                }
            },
            onReplyClick = { review ->
                Toast.makeText(this, "Balas review dari ${review.userName}", Toast.LENGTH_SHORT).show()
            },
            onMediaClick = { media ->
                Toast.makeText(this, "Media: ${media.type}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = reviewAdapter

        // Update count
        binding.tvReviewCount.text = " (${allReviews.size})"

        // Filter click
        binding.filterAll.setOnClickListener {
            isFilterMedia = false
            applyFilter()
            updateFilterUI()
        }
        binding.filterMedia.setOnClickListener {
            isFilterMedia = true
            applyFilter()
            updateFilterUI()
        }
    }

    private fun generateDummyReviews(): List<Review> {
        return listOf(
            Review(
                id = 1,
                userName = "Rizky F.",
                userAvatar = null,
                rating = 5.0f,
                date = "12 Jun 2026",
                comment = "Sepatu ini sangat nyaman dipakai lari maraton, solnya empuk banget! Desainnya juga keren dan tidak terlihat murahan. Recommended banget buat para runner pemula.",
                mediaList = listOf(
                    ReviewMedia("image", "https://placehold.co/150x150/eef2ff/333?text=Foto+1"),
                    ReviewMedia("image", "https://placehold.co/150x150/eef2ff/333?text=Foto+2"),
                    ReviewMedia("video", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                ),
                likeCount = 24,
                isLiked = false
            ),
            Review(
                id = 2,
                userName = "Sarah A.",
                userAvatar = null,
                rating = 5.0f,
                date = "02 Jun 2026",
                comment = "Pengiriman cepat, barang sesuai dengan gambar. Warna hitamnya elegant banget buat daily wear. Recommended!",
                mediaList = emptyList(),
                likeCount = 12,
                isLiked = false
            )
        )
    }

    private fun applyFilter() {
        filteredReviews = if (isFilterMedia) {
            allReviews.filter { it.mediaList.isNotEmpty() }
        } else {
            allReviews
        }
        reviewAdapter = ReviewAdapter(
            reviews = filteredReviews,
            onLikeClick = { review ->
                // Toggle like
                val updated = review.copy(
                    isLiked = !review.isLiked,
                    likeCount = if (review.isLiked) review.likeCount - 1 else review.likeCount + 1
                )
                val index = allReviews.indexOfFirst { it.id == review.id }
                if (index != -1) {
                    allReviews = allReviews.toMutableList().apply { set(index, updated) }
                    applyFilter()
                }
            },
            onReplyClick = { review ->
                Toast.makeText(this, "Balas review dari ${review.userName}", Toast.LENGTH_SHORT).show()
            },
            onMediaClick = { media ->
                Toast.makeText(this, "Media: ${media.type}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvReviews.adapter = reviewAdapter
    }

    private fun updateFilterUI() {
        if (isFilterMedia) {
            binding.filterAll.background = getDrawable(R.drawable.bg_filter_pill_inactive)
            binding.filterAll.setTextColor(resources.getColor(R.color.text_primary, theme))
            binding.filterMedia.background = getDrawable(R.drawable.bg_filter_pill_active)
            binding.filterMedia.setTextColor(resources.getColor(R.color.blue_primary, theme))
        } else {
            binding.filterAll.background = getDrawable(R.drawable.bg_filter_pill_active)
            binding.filterAll.setTextColor(resources.getColor(R.color.blue_primary, theme))
            binding.filterMedia.background = getDrawable(R.drawable.bg_filter_pill_inactive)
            binding.filterMedia.setTextColor(resources.getColor(R.color.text_primary, theme))
        }
    }
}