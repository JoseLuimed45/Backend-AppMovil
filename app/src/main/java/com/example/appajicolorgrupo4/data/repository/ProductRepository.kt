package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.ApiService
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.remote.SafeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductRepository(
    private val apiService: ApiService
) : SafeApiCall() {

    suspend fun getProducts(): NetworkResult<List<Product>> {
        return safeApiCall { apiService.getProductos() }
    }

    suspend fun getProductById(id: String): NetworkResult<Product> {
        return safeApiCall { apiService.getProductoById(id) }
    }

    suspend fun createProduct(
        nombre: String,
        descripcion: String,
        precio: Int,
        categoria: String,
        stock: Int,
        imageFile: File?
    ): NetworkResult<Product> {
        return safeApiCall {
            val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
            val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val catPart = categoria.toRequestBody("text/plain".toMediaTypeOrNull())
            val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val requestFile = it.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            apiService.createProduct(nombrePart, descPart, precioPart, catPart, stockPart, imagePart)
        }
    }

    suspend fun updateProduct(
        id: String,
        nombre: String,
        descripcion: String,
        precio: Int,
        categoria: String,
        stock: Int,
        imageFile: File?
    ): NetworkResult<Product> {
        return safeApiCall {
            val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
            val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val catPart = categoria.toRequestBody("text/plain".toMediaTypeOrNull())
            val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val requestFile = it.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            apiService.updateProduct(id, nombrePart, descPart, precioPart, catPart, stockPart, imagePart)
        }
    }

    suspend fun deleteProduct(id: String): NetworkResult<Unit> {
        return safeApiCall { apiService.deleteProduct(id) }
    }
}
