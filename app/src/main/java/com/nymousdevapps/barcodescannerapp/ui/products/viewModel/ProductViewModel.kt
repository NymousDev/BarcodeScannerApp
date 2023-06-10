package com.nymousdevapps.barcodescannerapp.ui.products.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nymousdevapps.barcodescannerapp.BarcodeScannerAppApplication
import com.nymousdevapps.barcodescannerapp.R
import com.nymousdevapps.barcodescannerapp.data.models.Product
import com.nymousdevapps.barcodescannerapp.data.room.interfaces.LocalDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel
@Inject
constructor(private val repository: LocalDatabaseRepository) : ViewModel() {
    val registerProduct: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val productsList: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>(listOf())
    }

    private var originalList: MutableList<Product> = mutableListOf()

    private fun validateFields(name: String, code: String, barcode: String): String {
        var isValid = ""
        if (name.isEmpty()) {
            isValid = BarcodeScannerAppApplication.resource?.getString(R.string.msg_val_name).toString()
            return isValid
        }
        if (code.isEmpty()) {
            isValid = BarcodeScannerAppApplication.resource?.getString(R.string.msg_val_code).toString()
            return isValid
        }
        if (barcode.isEmpty()) {
            isValid = BarcodeScannerAppApplication.resource?.getString(R.string.msg_val_barcode).toString()
            return isValid
        }
        return isValid
    }

    fun registerProduct(name: String, code: String, description: String, barcode: String) = viewModelScope.launch {
        if (validateFields(name, code, barcode) == "") {
            val result = repository.addLocalProduct(name, code, description, barcode)
            if (result.message?.isNotEmpty() == true) {
                errorMessage.value = result.message
            } else {
                registerProduct.value = true
            }
        }
    }

    fun getProducts() = viewModelScope.launch {
        val result = repository.getLocalProducts()
        if (result.message?.isNotEmpty() == true) {
            errorMessage.value = result.message
        } else {
            var products = mutableListOf<Product>()
            val localList = result.data
            if (localList != null) {
                for (items in localList) {
                    products.add(items.productEntityToProduct())
                }
            }
            productsList.value = products
            originalList = (productsList.value as MutableList<Product>).toMutableList()
        }
    }
}