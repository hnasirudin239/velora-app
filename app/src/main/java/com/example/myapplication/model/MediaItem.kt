package com.example.myapplication.model

import java.io.Serializable

data class MediaItem(
    val type: String, // "image" atau "video"
    val imageRes: Int? = null, // Untuk gambar (resource ID)
    val videoUrl: String? = null, // Untuk video (URL)
    val thumbnailRes: Int? = null // Thumbnail untuk video
) : Serializable