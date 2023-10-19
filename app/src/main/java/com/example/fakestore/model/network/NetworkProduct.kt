package com.example.fakestore.model.network

data class NetworkProduct(
 val id: Double,
 val title: String,
 val price: Double,
 val description: String,
 val category: String,
 val image: String,
 val rating: Rating
)

data class Rating(
 val rate: Double,
 val count: Double
)