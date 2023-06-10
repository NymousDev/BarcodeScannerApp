package com.nymousdevapps.barcodescannerapp.data.models

import java.io.Serializable

data class Product(
    val id: Int,
    var name: String,
    var code: String,
    var description: String,
    var barcode: String,
    val quantity: Int,
) : Serializable
