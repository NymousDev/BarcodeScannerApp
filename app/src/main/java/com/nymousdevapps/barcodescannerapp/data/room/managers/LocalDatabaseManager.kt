package com.nymousdevapps.barcodescannerapp.data.room.managers

import com.nymousdevapps.barcodescannerapp.Utils.Response
import com.nymousdevapps.barcodescannerapp.data.models.ProductEntity
import com.nymousdevapps.barcodescannerapp.data.room.db.BarcodeScannerDB
import com.nymousdevapps.barcodescannerapp.data.room.interfaces.LocalDatabaseRepository

class LocalDatabaseManager(private val localRoutesDB: BarcodeScannerDB) : LocalDatabaseRepository {
    override fun addLocalProduct(name: String, description: String, code: String, barcode: String) : Response<String> {
        return try {
            val newProduct = ProductEntity(0, name, code, description, barcode, 0)
            val result = localRoutesDB.productDao().addProduct(newProduct)
            return Response.Success(result.toString())
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    override fun getLocalProducts() : Response<List<ProductEntity>> {
        return try {
            val result = localRoutesDB.productDao().getAllProducts()
            return Response.Success(result)
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    override fun getLocalProductById(): Response<ProductEntity> {
        TODO("Not yet implemented")
    }

    override fun getLocalProductByBarcode(barcode: String): Response<ProductEntity> {
        return try {
            val result = localRoutesDB.productDao().getProductByBarcode(barcode)
            if (result != null) {
                return Response.Success(result)
            } else {
                Response.Error("No existe un producto con ese codigo de barras: ${barcode}", null)
            }
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    override fun editLocalProduct(product: ProductEntity) : Response<String> {
        return try {
            val result = localRoutesDB.productDao().updateProduct(product)
            return Response.Success(result.toString())
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    override fun editLocalProducts(products: List<ProductEntity>): Response<String> {
        return try {
            val result = localRoutesDB.productDao().updateProducts(products)
            return Response.Success(result.toString())
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    override fun deleteLocalProduct(product: ProductEntity) : Response<String> {
        TODO("Not yet implemented")
    }
}