package com.nymousdevapps.barcodescannerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.databinding.ProductItemBinding

class ProductAdapter(var productList: MutableList<Product>, val listener: IProductListener) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    interface IProductListener {
        fun onProductTap(product: Product)
    }

    class ProductViewHolder(val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.binding.productItem = productList[position]
        holder.binding.llContainer.setOnClickListener {
            listener.onProductTap(productList[position])
        }
    }

    override fun getItemCount(): Int {
        return  productList.size
    }

    fun updateList(productList: MutableList<Product>) {
        val oldSize = this.productList.size
        this.productList = productList
        if (productList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, productList.size)
        } else {
            notifyItemRangeChanged(0, this.productList.size)
        }
    }
}