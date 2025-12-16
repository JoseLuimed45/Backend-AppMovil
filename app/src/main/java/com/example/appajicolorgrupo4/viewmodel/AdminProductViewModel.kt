package com.example.appajicolorgrupo4.viewmodel

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

    private fun log(message: String) {
        try {
            android.util.Log.d("AdminProductVM", message)
        } catch (e: RuntimeException) {
            println("AdminProductVM: $message")
        }
    }

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
                    log("Productos cargados: ${_productos.value.size}")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al cargar productos"
                    log("Error: ${result.message}")
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
        log("crearProducto llamado con: nombre='$nombre', desc='$descripcion', precio=$precio, cat='$categoria', stock=$stock")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.createProduct(
                nombre, descripcion, precio, categoria, stock, imageFile
            )) {
                is NetworkResult.Success -> {
                    _successMessage.value = "Producto creado exitosamente"
                    cargarProductos() // Recargar lista
                    log("Producto creado: ${result.data?.nombre}")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al crear producto"
                    log("Error al crear: ${result.message}")
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
                    log("Producto actualizado: ${result.data?.nombre}")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al actualizar producto"
                    log("Error al actualizar: ${result.message}")
                }
                is NetworkResult.Loading -> {
                    // Ya está en loading
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Elimina un producto de forma segura, aceptando un ID que podría ser nulo.
     */
    fun eliminarProducto(id: String?) {
        // Validación defensiva: no hacer nada si el ID es nulo o está en blanco.
        if (id.isNullOrBlank()) {
            _error.value = "ID de producto inválido para eliminar."
            log("Se intentó eliminar un producto con ID nulo o vacío.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.deleteProduct(id)) {
                is NetworkResult.Success -> {
                    _successMessage.value = "Producto eliminado exitosamente"
                    cargarProductos() // Recargar lista
                    log("Producto eliminado: $id")
                }
                is NetworkResult.Error -> {
                    _error.value = result.message ?: "Error al eliminar producto"
                    log("Error al eliminar: ${result.message}")
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

