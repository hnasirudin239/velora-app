package com.example.myapplication.model

import java.io.Serializable

data class ReviewMedia(
    val type: String, // "image" atau "video"
    val url: String, // URL gambar/video
    val thumbnailUrl: String? = null // thumbnail untuk video
) : Serializable