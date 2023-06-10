package com.nymousdevapps.barcodescannerapp.ui.products.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nymousdevapps.barcodescannerapp.Utils.BarcodeItem
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.data.models.ProductEntity
import com.nymousdevapps.barcodescannerapp.data.room.interfaces.LocalDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductCartViewModel
@Inject
constructor(private val repository: LocalDatabaseRepository) : ViewModel() {
    val updateProduct: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val productsListScanned: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>(listOf())
    }

    fun getProductByBarcode(list: ArrayList<BarcodeItem>) = viewModelScope.launch {
        var products = mutableListOf<Product>()
        for (scanned in list) {
            val result = repository.getLocalProductByBarcode(scanned.barcode)
            if (result.message?.isNotEmpty() == true) {
                errorMessage.value = result.message
            } else {
                val data = result.data
                if (productsListScanned.value?.isNotEmpty() == true) {
                    products = productsListScanned.value!!.toMutableList()
                    for ((index, currentItem) in productsListScanned.value!!.toList().withIndex()) {
                        if (currentItem.barcode == data?.barcode) {
                            val newQuantity = currentItem.quantity + scanned.quantity
                            val edtPro = Product(currentItem.id, currentItem.name, currentItem.code, currentItem.description, currentItem.barcode, newQuantity)
                            products[index] = edtPro
                        } else {
                            val pro = products.find { it.barcode == data?.barcode }
                            if (pro == null) {
                                val newPro = Product(data?.id ?: 0, data?.name ?: "", data?.code ?: "", data?.description ?: "", data?.barcode ?: "", scanned.quantity)
                                products.add(newPro)
                            }
                        }
                    }
                } else {
                    val newPro = Product(data?.id ?: 0, data?.name ?: "", data?.code ?: "", data?.description ?: "", data?.barcode ?: "", scanned.quantity)
                    products.add(newPro)
                }
            }
        }
        productsListScanned.value = products
    }

    fun updateProductQuantities(products: List<Product>) = viewModelScope.launch {
        val productsEdited = mutableListOf<ProductEntity>()
        for (product in products) {
            val result = repository.getLocalProductByBarcode(product.barcode)
            if (result.message?.isNotEmpty() == true) {
                errorMessage.value = result.message
            } else {
                val data = result.data
                val finalQuantity = (data?.quantity ?: 0) - product.quantity
                val newProQuan = ProductEntity(data?.id ?: 0, data?.name ?: "", data?.code ?: "", data?.description ?: "", data?.barcode ?: "", finalQuantity)
                productsEdited.add(newProQuan)
            }
        }
        editProductQuantities(productsEdited.toList())
    }

    private fun editProductQuantities(products: List<ProductEntity>) = viewModelScope.launch {
        val result = repository.editLocalProducts(products)
        if (result.message?.isNotEmpty() == true) {
            errorMessage.value = result.message
        } else {
            updateProduct.value = true
        }
    }

    fun clearList() {
        val products = mutableListOf<Product>()
        productsListScanned.value = products
    }
}