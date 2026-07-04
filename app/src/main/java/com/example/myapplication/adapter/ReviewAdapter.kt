package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemReviewBinding
import com.example.myapplication.model.Review
import com.example.myapplication.model.ReviewMedia

class ReviewAdapter(
    private val reviews: List<Review>,
    private val onLikeClick: (Review) -> Unit,
    private val onReplyClick: (Review) -> Unit,
    private val onMediaClick: (ReviewMedia) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.binding.apply {
            // Avatar
            if (review.userAvatar.isNullOrEmpty()) {
                // Gunakan inisial
                val initial = review.userName.take(1).uppercase()
                ivAvatar.setImageResource(R.drawable.ic_profile_avatar)
                // Alternatif: set text di ShapeableImageView tidak langsung, kita gunakan TextView
                // Untuk sederhana, pakai icon default
            } else {
                // Load dengan Glide
            }

            tvUserName.text = review.userName
            tvReviewDate.text = review.date
            rbReviewRating.rating = review.rating
            tvReviewComment.text = review.comment

            // Like
            val likeText = if (review.isLiked) "❤️ Suka (${review.likeCount})" else "Suka (${review.likeCount})"
            tvLike.text = likeText
            tvLike.setOnClickListener {
                onLikeClick(review)
            }

            // Reply
            tvReply.setOnClickListener {
                onReplyClick(review)
            }

            // Media
            if (review.mediaList.isNotEmpty()) {
                rvReviewMedia.visibility = android.view.View.VISIBLE
                val mediaAdapter = ReviewMediaAdapter(review.mediaList) { media ->
                    onMediaClick(media)
                }
                rvReviewMedia.layoutManager = LinearLayoutManager(
                    holder.itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                rvReviewMedia.adapter = mediaAdapter
            } else {
                rvReviewMedia.visibility = android.view.View.GONE
            }
        }
    }

    override fun getItemCount(): Int = reviews.size
}