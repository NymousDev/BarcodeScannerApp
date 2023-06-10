package com.nymousdevapps.barcodescannerapp.ui.products.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.data.models.ProductEntity
import com.nymousdevapps.barcodescannerapp.data.room.interfaces.LocalDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel
@Inject
constructor(private val repository: LocalDatabaseRepository) : ViewModel() {
    val updateProduct: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun updateProduct(product: Product, quantity: Int) = viewModelScope.launch {
        val finalStock = product.quantity + quantity
        val productEdited = ProductEntity(product.id, product.name, product.code, product.description, product.barcode, finalStock)
        val result = repository.editLocalProduct(productEdited)
        if (result.message?.isNotEmpty() == true) {
            errorMessage.value = result.message
        } else {
            updateProduct.value = true
        }
    }
}