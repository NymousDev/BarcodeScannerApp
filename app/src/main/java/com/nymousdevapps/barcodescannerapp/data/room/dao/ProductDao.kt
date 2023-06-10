package com.nymousdevapps.barcodescannerapp.data.room.dao

import androidx.room.*
import com.nymousdevapps.barcodescannerapp.data.models.ProductEntity

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(productEntity: ProductEntity)

    @Query("SELECT * FROM productentity")
    fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM productentity WHERE id=:productId")
    fun getProductById(productId: Int): ProductEntity

    @Query("SELECT * FROM productentity WHERE barcode=:barcode")
    fun getProductByBarcode(barcode: String): ProductEntity

    @Update
    fun updateProduct(productEntity: ProductEntity)

    @Update
    fun updateProducts(productEntity: List<ProductEntity>)

    @Delete
    fun deleteProduct(productEntity: ProductEntity)
}