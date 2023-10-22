package com.example.fakestore.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fakestore.ProductsService
import com.example.fakestore.databinding.FragmentFirstBinding
import com.example.fakestore.model.mapper.ProductMapper
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class FirstFragment : Fragment(){

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
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
        shimmerFrameLayout = binding.shimmerContainer

        lifecycleScope.launch { loadDataAndDisplayRV() }

    }

    private fun loadDataAndDisplayRV() {
        lifecycleScope.launch {
            try {
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
                val productsList = withContext(IO) {
                    productsService.getAllProducts().body()!!.map { ProductMapper.buildFrom(it) }
                }
                productsAdapter.submitData(productsList)
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
            } catch (e: Exception) {
                val handler = Handler(Looper.getMainLooper())

                handler.postDelayed({
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    handleException(binding.root, e)
                }, 2000)

            }
        }
    }

    private fun handleException(view: View, e: Exception) {
        when (e) {
            is IOException -> {
                Log.e("FirstFragment", "IOException: ${e.message}")
                showSnackbar(view, "Internet connection problem.")
            }
            else -> {
                Log.e("FirstFragment", "Exception: ${e.message}")
                showSnackbar(view, "Timeout error.")
            }
        }
    }

    private fun showSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Refresh") {
            loadDataAndDisplayRV()
        }
        snackbar.show()
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