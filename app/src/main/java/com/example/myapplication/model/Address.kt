package com.example.myapplication.model

import java.io.Serializable

data class Address(
    val id: Int,
    val name: String,
    val phone: String,
    val address: String,
    val isDefault: Boolean = false
) : Serializable