package com.example.appajicolorgrupo4.data.repository

import android.util.Log
import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.ApiService
import com.example.appajicolorgrupo4.data.remote.CreateProductRequest
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.remote.SafeApiCall
import com.example.appajicolorgrupo4.data.remote.UpdateProductRequest
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProductRepository(private val apiService: ApiService) : SafeApiCall() {

    suspend fun getProducts(): NetworkResult<List<Product>> {
        Log.d("ProductRepo", "Fetching products from /api/v1/productos")
        return safeApiCall {
            Log.d("ProductRepo", "Making API call...")
            apiService.getProductos()
        }
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
        Log.d(
                "ProductRepo",
                "createProduct: nombre='$nombre', desc='$descripcion', precio=$precio, cat='$categoria', stock=$stock, image=${imageFile?.name}"
        )
        return safeApiCall {
            // Generar un ID único para el producto
            val productId =
                    "prod-${System.currentTimeMillis()}-${kotlin.random.Random.nextInt(10000)}"

            if (imageFile == null) {
                // Sin imagen: usar endpoint JSON
                Log.d("ProductRepo", "Using JSON endpoint (no image) with ID: $productId")
                val request =
                        CreateProductRequest(
                                id = productId,
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precio,
                                categoria = categoria,
                                stock = stock
                        )
                Log.d("ProductRepo", "CreateProductRequest: $request")
                apiService.createProductJson(request)
            } else {
                // Con imagen: usar endpoint multipart
                Log.d("ProductRepo", "Using multipart endpoint (with image) with ID: $productId")
                val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
                val descPart = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
                val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val catPart = categoria.toRequestBody("text/plain".toMediaTypeOrNull())
                val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val idPart = productId.toRequestBody("text/plain".toMediaTypeOrNull())

                val requestFile = imageFile.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                        MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                apiService.createProduct(
                        idPart,
                        nombrePart,
                        descPart,
                        precioPart,
                        catPart,
                        stockPart,
                        imagePart
                )
            }
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
        Log.d(
                "ProductRepo",
                "updateProduct: id=$id, nombre='$nombre', desc='$descripcion', precio=$precio, cat='$categoria', stock=$stock, image=${imageFile?.name}"
        )
        return safeApiCall {
            if (imageFile == null) {
                // Sin imagen: usar endpoint JSON
                Log.d("ProductRepo", "Using JSON endpoint (no image)")
                val request =
                        UpdateProductRequest(
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precio,
                                categoria = categoria,
                                stock = stock
                        )
                apiService.updateProductJson(id, request)
            } else {
                // Con imagen: usar endpoint multipart
                Log.d("ProductRepo", "Using multipart endpoint (with image)")
                val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
                val descPart = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
                val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val catPart = categoria.toRequestBody("text/plain".toMediaTypeOrNull())
                val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val requestFile = imageFile.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                        MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                apiService.updateProduct(
                        id,
                        nombrePart,
                        descPart,
                        precioPart,
                        catPart,
                        stockPart,
                        imagePart
                )
            }
        }
    }

    suspend fun deleteProduct(id: String): NetworkResult<Unit> {
        return safeApiCall { apiService.deleteProduct(id) }
    }
}
