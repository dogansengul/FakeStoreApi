package com.example.fakestore.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.fakestore.databinding.RvItemProductBinding
import com.example.fakestore.model.domain.Product


class ProductsAdapter : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    private var productsData = ArrayList<Product>()
    private var selectedItemPosition = RecyclerView.NO_POSITION


    inner class ProductsViewHolder(private val binding: RvItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val root = binding.root
        private val imageView = binding.shapeableImageView
        private val tvTitle = binding.tvTitle
        private val tvCategory = binding.tvCategory
        private val tvPrice = binding.tvPrice
        private val tvDescription = binding.tvDescription

        init {
            root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (position == selectedItemPosition) {
                        // Seçili öğe zaten tıklanmış, açık olanı kapat
                        selectedItemPosition = RecyclerView.NO_POSITION
                        binding.tvDescription.visibility = View.GONE
                    } else {
                        // Yeni bir öğe tıklandı, önceki açık olanı kapat
                        val previousPosition = selectedItemPosition
                        selectedItemPosition = position
                        notifyItemChanged(previousPosition)
                        binding.tvDescription.visibility = View.VISIBLE
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            Glide.with(binding.root)
                .load(product.image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        Log.d("LoadFailedError", "Something went wrong with the image loading.")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                }).into(imageView)
            tvTitle.text = product.title
            tvCategory.text = product.category
            tvPrice.text = "${product.price} $"
            tvDescription.text = product.description

            // Seçili öğe ise açık olanı göster
            if (adapterPosition == selectedItemPosition) {
                binding.tvDescription.visibility = View.VISIBLE
            } else {
                binding.tvDescription.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(RvItemProductBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return productsData.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind(productsData[position])

    }

    fun submitData(newData: List<Product>) {
        val diffUtil = ProductsAdapterDiffUtil(productsData, newData)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        productsData.clear()
        productsData.addAll(newData)
        diffResults.dispatchUpdatesTo(this)
    }

}
