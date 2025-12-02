package com.example.appajicolorgrupo4.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel para administración de productos (solo para usuarios admin)
 */
class AdminProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Product>>(emptyList())
    val productos: StateFlow<List<Product>> = _productos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        cargarProductos()
    }

    /**
     * Carga todos los productos desde el backend
     */
    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.getProducts()) {
                is NetworkResult.Success -> {
                    _productos.value = result.data ?: emptyList()
                    Log.d("AdminProductVM", "Productos cargados: ${_productos.value.size}")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al cargar productos"
                    Log.e("AdminProductVM", "Error: ${result.message}")
                }
                is NetworkResult.Loading -> {
                    // Ya está en loading
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Crea un nuevo producto
     */
    fun crearProducto(
        nombre: String,
        descripcion: String,
        precio: Int,
        categoria: String,
        stock: Int,
        imageFile: File? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.createProduct(
                nombre, descripcion, precio, categoria, stock, imageFile
            )) {
                is NetworkResult.Success -> {
                    _successMessage.value = "Producto creado exitosamente"
                    cargarProductos() // Recargar lista
                    Log.d("AdminProductVM", "Producto creado: ${result.data?.nombre}")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al crear producto"
                    Log.e("AdminProductVM", "Error al crear: ${result.message}")
                }
                is NetworkResult.Loading -> {
                    // Ya está en loading
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Actualiza un producto existente
     */
    fun actualizarProducto(
        id: String,
        nombre: String,
        descripcion: String,
        precio: Int,
        categoria: String,
        stock: Int,
        imageFile: File? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.updateProduct(
                id, nombre, descripcion, precio, categoria, stock, imageFile
            )) {
                is NetworkResult.Success -> {
                    _successMessage.value = "Producto actualizado exitosamente"
                    cargarProductos() // Recargar lista
                    Log.d("AdminProductVM", "Producto actualizado: ${result.data?.nombre}")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al actualizar producto"
                    Log.e("AdminProductVM", "Error al actualizar: ${result.message}")
                }
                is NetworkResult.Loading -> {
                    // Ya está en loading
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Elimina un producto
     */
    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.deleteProduct(id)) {
                is NetworkResult.Success -> {
                    _successMessage.value = "Producto eliminado exitosamente"
                    cargarProductos() // Recargar lista
                    Log.d("AdminProductVM", "Producto eliminado: $id")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al eliminar producto"
                    Log.e("AdminProductVM", "Error al eliminar: ${result.message}")
                }
                is NetworkResult.Loading -> {
                    // Ya está en loading
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }
}

