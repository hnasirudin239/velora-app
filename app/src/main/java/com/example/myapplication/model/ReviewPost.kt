package com.example.myapplication.model

import android.net.Uri
import java.io.Serializable

data class ReviewPost(
    val productId: Int,
    val rating: Float,
    val comment: String,
    val mediaUris: List<Uri> = emptyList()
) : Serializable