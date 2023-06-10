package com.nymousdevapps.barcodescannerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.databinding.ProductScannedItemBinding

class ProductScannerAdapter(var productList: MutableList<Product>, val listener: IProductScannerListener) : RecyclerView.Adapter<ProductScannerAdapter.ProductScannerViewHolder>() {
    interface IProductScannerListener {
        fun onProductTap(product: Product)
    }

    class ProductScannerViewHolder(val binding: ProductScannedItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductScannerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductScannedItemBinding.inflate(inflater, parent, false)
        return ProductScannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductScannerViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.txtName.text = product.name
        holder.binding.txtBarcode.text = product.barcode
        holder.binding.txtQuantity.text = product.quantity.toString()
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