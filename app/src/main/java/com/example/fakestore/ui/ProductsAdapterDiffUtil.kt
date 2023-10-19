package com.example.fakestore.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.fakestore.model.domain.Product

class ProductsAdapterDiffUtil(private val productsData: ArrayList<Product>, private val newData: List<Product>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return productsData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return productsData[oldItemPosition].id == newData[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return productsData[oldItemPosition] == newData[newItemPosition]
    }


}
