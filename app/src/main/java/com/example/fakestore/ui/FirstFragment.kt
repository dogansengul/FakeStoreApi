package com.example.fakestore.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fakestore.databinding.FragmentFirstBinding
import com.example.fakestore.model.FirstViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FirstFragment : Fragment(){

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private val viewModel: FirstViewModel by viewModels()

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

        prepareRecyclerView()
        shimmerFrameLayout = binding.shimmerContainer

        loadDataToRV()

    }

    private fun loadDataToRV() {
        viewModel.loadProducts()
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()

        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            val productsList = it.products
            val errorMessage = it.error
            if (errorMessage.isNullOrBlank()) {
                shimmerFrameLayout.visibility = View.GONE
                shimmerFrameLayout.stopShimmer()
                binding.rvProducts.visibility = View.VISIBLE
                productsAdapter.submitData(productsList!!)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    shimmerFrameLayout.visibility = View.GONE
                    shimmerFrameLayout.stopShimmer()
                    showSnackbar(binding.root, errorMessage)
                }, 2000)
            }
        }
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Refresh") {
            loadDataToRV()
        }.show()
    }

    private fun prepareRecyclerView() {
        productsAdapter = ProductsAdapter()
        binding.rvProducts.apply {
            adapter = productsAdapter
            layoutManager = GridLayoutManager(context, 1)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}