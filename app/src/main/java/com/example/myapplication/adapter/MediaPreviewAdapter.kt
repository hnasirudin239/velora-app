package com.example.myapplication.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemMediaPreviewBinding

class MediaPreviewAdapter(
    private val mediaUris: MutableList<Uri>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<MediaPreviewAdapter.MediaViewHolder>() {

    class MediaViewHolder(val binding: ItemMediaPreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val uri = mediaUris[position]
        val isVideo = uri.toString().contains("video")

        // Load dengan Glide atau setImageURI
        holder.binding.ivMediaPreview.setImageURI(uri)
        /*Glide.with(holder.itemView.context)
            .load(uri)
            .centerCrop()
            .into(holder.binding.ivMediaPreview)*/

        if (isVideo) {
            holder.binding.tvVideoBadge.visibility = android.view.View.VISIBLE
        } else {
            holder.binding.tvVideoBadge.visibility = android.view.View.GONE
        }

        holder.binding.btnRemoveMedia.setOnClickListener {
            onRemove(position)
        }
    }

    override fun getItemCount(): Int = mediaUris.size
}