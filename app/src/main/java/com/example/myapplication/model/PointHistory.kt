package com.example.myapplication.model

data class PointHistory(
    val id: Int,
    val title: String,
    val date: String,
    val points: Int, // positif = earned, negatif = spent
    val type: String // "earn", "spend", "expire"
)