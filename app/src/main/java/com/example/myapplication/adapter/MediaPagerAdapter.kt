package com.example.myapplication.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemMediaViewpagerBinding
import com.example.myapplication.model.MediaItem

class MediaPagerAdapter(
    private val mediaList: List<MediaItem>
) : RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder>() {

    private var videoView: VideoView? = null
    private var currentPlayingPosition = -1

    class MediaViewHolder(val binding: ItemMediaViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val ivImage: ImageView = binding.ivMediaImage
        val vvVideo: VideoView = binding.vvMediaVideo
        val progressBar: ProgressBar = binding.progressBar
        val ivPlayButton: ImageView = binding.ivPlayButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaViewpagerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]

        // Reset semua view
        holder.ivImage.visibility = View.GONE
        holder.vvVideo.visibility = View.GONE
        holder.progressBar.visibility = View.GONE
        holder.ivPlayButton.visibility = View.GONE

        when (media.type) {
            "image" -> {
                // Tampilkan gambar
                holder.ivImage.visibility = View.VISIBLE
                media.imageRes?.let {
                    holder.ivImage.setImageResource(it)
                }
            }
            "video" -> {
                // Tampilkan video
                holder.vvVideo.visibility = View.VISIBLE
                holder.ivPlayButton.visibility = View.VISIBLE
                holder.progressBar.visibility = View.VISIBLE

                media.videoUrl?.let { url ->
                    val videoUri = Uri.parse(url)
                    holder.vvVideo.setVideoURI(videoUri)
                }

                // Setup video controls
                setupVideoView(holder)
            }
        }
    }

    private fun setupVideoView(holder: MediaViewHolder) {
        // Progress bar hilang saat video siap
        holder.vvVideo.setOnPreparedListener { mp ->
            holder.progressBar.visibility = View.GONE
            // Atur ukuran video agar proporsional
            val videoWidth = mp.videoWidth
            val videoHeight = mp.videoHeight
            val viewWidth = holder.vvVideo.width
            val viewHeight = holder.vvVideo.height

            if (viewWidth > 0 && viewHeight > 0) {
                val ratio = videoWidth.toFloat() / videoHeight.toFloat()
                val newWidth = (viewHeight * ratio).toInt()
                val newHeight = viewHeight
                holder.vvVideo.layoutParams.width = newWidth
                holder.vvVideo.layoutParams.height = newHeight
            }
        }

        // Play button click
        holder.ivPlayButton.setOnClickListener {
            if (holder.vvVideo.isPlaying) {
                holder.vvVideo.pause()
                holder.ivPlayButton.setImageResource(R.drawable.ic_play_circle)
            } else {
                holder.vvVideo.start()
                holder.ivPlayButton.setImageResource(R.drawable.ic_pause_circle)
                // Sembunyikan play button saat video diputar
                holder.ivPlayButton.visibility = View.GONE
            }
        }

        // Selesai diputar, tampilkan play button lagi
        holder.vvVideo.setOnCompletionListener {
            holder.ivPlayButton.visibility = View.VISIBLE
            holder.ivPlayButton.setImageResource(R.drawable.ic_play_circle)
        }

        // Error handling
        holder.vvVideo.setOnErrorListener { _, what, extra ->
            holder.progressBar.visibility = View.GONE
            holder.ivPlayButton.visibility = View.GONE
            true
        }
    }

    override fun getItemCount(): Int = mediaList.size

    // Fungsi untuk pause video saat fragment/activity paused
    fun pauseAllVideos() {
        videoView?.pause()
    }

    // Fungsi untuk release video saat fragment/activity destroyed
    fun releaseAllVideos() {
        videoView?.stopPlayback()
        videoView = null
    }
}