package com.example.fakestore.model

import android.util.Log
import com.example.fakestore.ProductsService
import com.example.fakestore.model.domain.Product
import com.example.fakestore.model.mapper.ProductMapper
import com.example.fakestore.model.network.NetworkProduct
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val productsService: ProductsService)  {

    suspend fun getAllProducts(): List<Product> = productsService.getAllProducts().body()!!
            .map<NetworkProduct, Product> { ProductMapper.buildFrom(it) }

}
