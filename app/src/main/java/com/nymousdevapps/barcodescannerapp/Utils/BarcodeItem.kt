package com.nymousdevapps.barcodescannerapp.Utils

import java.io.Serializable

data class BarcodeItem(
    val barcode: String,
    var quantity: Int,
) : Serializable {
}