package com.example.fakestore.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fakestore.ProductsService
import com.example.fakestore.R
import com.example.fakestore.databinding.FragmentFirstBinding
import com.example.fakestore.model.domain.Product
import com.example.fakestore.model.mapper.ProductMapper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FirstFragment : Fragment(){

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsAdapter: ProductsAdapter
    @Inject lateinit var productsService: ProductsService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.button.setOnClickListener {
        //    Navigation.findNavController(binding.root).navigate(R.id.action_firstFragment_to_secondFragment)
        //}
        prepareRecyclerView()

        lifecycleScope.launch {
            try {
                val productsList: List<Product> = productsService.getAllProducts().body()!!.map { ProductMapper.buildFrom(it) }
                productsAdapter.submitData(productsList)
            } catch (e: TimeoutCancellationException) {
                val snackbar = Snackbar.make(view, "Zaman aşımı hatası! Yeniden denemek için tıklayın.", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Yeniden Dene") {
                    // Yenileme işlemi burada yapılacak
                    // Örnek: refreshData() metodunu çağırabilirsiniz
                    CoroutineScope(IO).launch {
                        loadData(view)
                    }
                }
                snackbar.show()
            }
        }
    }

    private suspend fun loadData(view: View) {
        try {
            val productsList: List<Product> = productsService.getAllProducts().body()!!.map { ProductMapper.buildFrom(it) }
            productsAdapter.submitData(productsList)
        } catch (e: TimeoutCancellationException) {
            val snackbar = Snackbar.make(view, "Zaman aşımı hatası! Yeniden denemek için tıklayın.", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Yeniden Dene") {
                // Yenileme işlemi burada yapılacak
                // Örnek: refreshData() metodunu çağırabilirsiniz
                CoroutineScope(IO).launch {
                    loadData(view)
                }
            }
            snackbar.show()
        }
    }

    private fun prepareRecyclerView() {
        productsAdapter = ProductsAdapter()
        binding.rvProducts.apply {
            adapter = productsAdapter
            layoutManager = GridLayoutManager(context, 1)
        }
        binding.rvProducts
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}