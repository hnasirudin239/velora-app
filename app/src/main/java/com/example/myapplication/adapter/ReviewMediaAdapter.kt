package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemReviewMediaBinding
import com.example.myapplication.model.ReviewMedia

class ReviewMediaAdapter(
    private val mediaList: List<ReviewMedia>,
    private val onItemClick: (ReviewMedia) -> Unit
) : RecyclerView.Adapter<ReviewMediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(val binding: ItemReviewMediaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemReviewMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        holder.binding.apply {
            // Load gambar dengan Glide atau setImageResource
            // Jika menggunakan drawable lokal, gunakan setImageResource
            // Untuk demo, kita pakai placeholder
            ivMedia.setImageResource(R.drawable.ic_launcher_foreground)

            if (media.type == "video") {
                ivVideoBadge.visibility = View.VISIBLE
            } else {
                ivVideoBadge.visibility = View.GONE
            }

            root.setOnClickListener {
                onItemClick(media)
            }
        }
    }

    override fun getItemCount(): Int = mediaList.size
}