package com.example.myapplication.model

import java.io.Serializable

data class Review(
    val id: Int,
    val userName: String,
    val userAvatar: String? = null,
    val rating: Float,
    val date: String,
    val comment: String,
    val mediaList: List<ReviewMedia> = emptyList(),
    val likeCount: Int,
    val isLiked: Boolean = false
) : Serializable