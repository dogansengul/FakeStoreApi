package com.example.fakestore.model.domain

import com.example.fakestore.model.network.Rating

data class Product(
    val id: Double,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
)
