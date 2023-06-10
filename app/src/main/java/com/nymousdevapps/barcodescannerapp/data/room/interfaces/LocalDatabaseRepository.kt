package com.nymousdevapps.barcodescannerapp.data.room.interfaces

import com.nymousdevapps.barcodescannerapp.Utils.Response
import com.nymousdevapps.barcodescannerapp.data.models.ProductEntity

interface LocalDatabaseRepository {
    fun addLocalProduct(name: String, description: String, code: String, barcode: String) : Response<String>
    fun getLocalProducts() : Response<List<ProductEntity>>
    fun getLocalProductById() : Response<ProductEntity>
    fun getLocalProductByBarcode(barcode: String) : Response<ProductEntity>
    fun editLocalProduct(product: ProductEntity) : Response<String>
    fun editLocalProducts(products: List<ProductEntity>) : Response<String>
    fun deleteLocalProduct(product: ProductEntity) : Response<String>
}