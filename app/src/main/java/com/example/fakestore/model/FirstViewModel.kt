package com.example.fakestore.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.SingleLiveEvent
import com.example.fakestore.model.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(private val productsRepository: ProductsRepository) : ViewModel() {

    private val _productsLiveData: SingleLiveEvent<DataWrapper> = SingleLiveEvent()
    val productsLiveData: LiveData<DataWrapper> = _productsLiveData

    data class DataWrapper(
        val products: List<Product>? = null,
        val error: String? = null
    )

    fun loadProducts() {
        viewModelScope.launch {
            try {
                val products = productsRepository.getAllProducts()
                _productsLiveData.postValue(DataWrapper(products = products, error = null))
                Log.d("Kontrol", "veriler aktarıldı")
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is IOException -> {
                        Log.d("Kontrol", "internet yok")
                        "Internet connection problem."
                    }
                    else -> {
                        Log.d("Kontrol", "timeout")
                        "Timeout error."
                    }
                }
                _productsLiveData.postValue(DataWrapper(products = null, error = errorMessage))
            }
        }
    }
}