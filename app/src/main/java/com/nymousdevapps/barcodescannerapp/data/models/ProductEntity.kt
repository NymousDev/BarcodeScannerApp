package com.nymousdevapps.barcodescannerapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var code: String,
    var description: String,
    var barcode: String,
    var quantity: Int,
) {
    fun productEntityToProduct(): Product {
        return Product(id, name, code, description, barcode, quantity)
    }
}