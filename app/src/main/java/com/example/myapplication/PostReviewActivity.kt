package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.MediaPreviewAdapter
import com.example.myapplication.databinding.ActivityPostReviewBinding
import com.example.myapplication.model.ReviewPost

class PostReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostReviewBinding
    private var currentRating = 0f
    private val mediaUris = mutableListOf<Uri>()
    private lateinit var mediaAdapter: MediaPreviewAdapter
    private val MAX_FILES = 6

    private val mediaPickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    addMediaUri(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStars()
        setupMediaGrid()
        setupListeners()
    }

    private fun setupStars() {
        val starContainer = binding.starContainer
        for (i in 1..5) {
            val star = TextView(this).apply {
                text = "⭐"
                textSize = 36f
                setTextColor(ContextCompat.getColor(this@PostReviewActivity, R.color.gray_star))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(2, 0, 2, 0)
                }
                setOnClickListener {
                    currentRating = i.toFloat()
                    updateStars()
                }
                tag = i
            }
            starContainer.addView(star)
        }
        updateStars()
    }

    private fun updateStars() {
        for (i in 0 until binding.starContainer.childCount) {
            val star = binding.starContainer.getChildAt(i) as TextView
            val starValue = star.tag as Int
            val color = if (starValue <= currentRating) {
                ContextCompat.getColor(this, R.color.yellow_star)
            } else {
                ContextCompat.getColor(this, R.color.gray_star)
            }
            star.setTextColor(color)
        }
    }

    private fun setupMediaGrid() {
        mediaAdapter = MediaPreviewAdapter(mediaUris) { position ->
            mediaUris.removeAt(position)
            mediaAdapter.notifyDataSetChanged()
            updateAddMediaButton()
        }
        binding.rvMediaPreview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMediaPreview.adapter = mediaAdapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvCancel.setOnClickListener {
            finish()
        }

        binding.btnAddMedia.setOnClickListener {
            if (mediaUris.size >= MAX_FILES) {
                Toast.makeText(this, "Maksimal 6 file", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            openMediaPicker()
        }

        binding.etReviewText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val length = s?.length ?: 0
                binding.tvCharCount.text = "$length / 500"
            }
        })

        binding.btnSubmitReview.setOnClickListener { view ->
            animateView(view)
            submitReview()
        }
    }

    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/* video/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        }
        mediaPickerLauncher.launch(intent)
    }

    private fun addMediaUri(uri: Uri) {
        if (mediaUris.size >= MAX_FILES) {
            Toast.makeText(this, "Maksimal 6 file", Toast.LENGTH_SHORT).show()
            return
        }
        mediaUris.add(uri)
        mediaAdapter.notifyDataSetChanged()
        updateAddMediaButton()
    }

    private fun updateAddMediaButton() {
        if (mediaUris.size >= MAX_FILES) {
            binding.btnAddMedia.visibility = View.GONE
        } else {
            binding.btnAddMedia.visibility = View.VISIBLE
        }
    }

    private fun submitReview() {
        val comment = binding.etReviewText.text.toString().trim()

        if (currentRating == 0f) {
            Toast.makeText(this, "Harap berikan rating bintang", Toast.LENGTH_SHORT).show()
            return
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Harap tulis ulasan Anda", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = intent.getIntExtra("productId", 1)

        val reviewPost = ReviewPost(
            productId = productId,
            rating = currentRating,
            comment = comment,
            mediaUris = mediaUris.toList()
        )

        val resultIntent = Intent().apply {
            putExtra("review", reviewPost)
        }
        setResult(RESULT_OK, resultIntent)

        Toast.makeText(
            this,
            "✅ Ulasan berhasil dikirim!\nRating: ${currentRating.toInt()} bintang",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    private fun animateView(view: View) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
        }.start()
    }
}