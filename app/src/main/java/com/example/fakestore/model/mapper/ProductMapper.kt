package com.example.fakestore.model.mapper

import com.example.fakestore.model.domain.Product
import com.example.fakestore.model.network.NetworkProduct

class ProductMapper() {

    companion object {
        fun buildFrom(networkProduct: NetworkProduct): Product {
            return Product(
                id = networkProduct.id,
                title = networkProduct.title,
                price = networkProduct.price,
                description = networkProduct.description,
                category = networkProduct.category,
                image = networkProduct.image)
        }
    }
}