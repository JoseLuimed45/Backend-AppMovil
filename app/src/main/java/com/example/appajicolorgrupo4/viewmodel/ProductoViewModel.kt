package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appajicolorgrupo4.data.*
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CatalogoUiState(
    val productos: List<Producto> = emptyList(),
    val searchQuery: String = "",
    val categoriaFiltro: CategoriaProducto? = null,
    val isSearchActive: Boolean = false
)

data class DetalleProductoUiState(
    val producto: Producto? = null,
    val cantidad: Int = 1,
    val tallaSeleccionada: Talla? = null,
    val colorSeleccionado: ColorInfo? = null,
    val showSnackbar: Boolean = false
)

class ProductoViewModel(private val mainViewModel: MainViewModel) : ViewModel() {

    private val _allProductos = CatalogoProductos.obtenerTodos()
    private val _catalogoState = MutableStateFlow(CatalogoUiState(productos = _allProductos))
    val catalogoState: StateFlow<CatalogoUiState> = _catalogoState.asStateFlow()

    private val _detalleState = MutableStateFlow(DetalleProductoUiState())
    val detalleState: StateFlow<DetalleProductoUiState> = _detalleState.asStateFlow()

    // --- Lógica de Catálogo ---
    fun onQueryChange(query: String) {
        _catalogoState.update { it.copy(searchQuery = query) }
        filterProducts()
    }

    fun onCategoriaChange(categoria: CategoriaProducto?) {
        _catalogoState.update { it.copy(categoriaFiltro = categoria) }
        filterProducts()
    }

    fun onSearchActiveChange(isActive: Boolean) {
        _catalogoState.update { it.copy(isSearchActive = isActive) }
        if (!isActive) onQueryChange("")
    }

    private fun filterProducts() {
        val state = _catalogoState.value
        val filtered = _allProductos.filter { producto ->
            val matchesCategory = state.categoriaFiltro == null || producto.categoria == state.categoriaFiltro
            val matchesQuery = state.searchQuery.isBlank() ||
                               producto.nombre.contains(state.searchQuery, true) ||
                               producto.descripcion.contains(state.searchQuery, true)
            matchesCategory && matchesQuery
        }
        _catalogoState.update { it.copy(productos = filtered) }
    }

    // --- Lógica de Detalle ---
    fun onProductoSelected(productoId: String) {
        val producto = _allProductos.find { it.id == productoId }
        _detalleState.value = DetalleProductoUiState(producto = producto)
    }

    fun onCantidadChange(cantidad: Int) {
        val stock = _detalleState.value.producto?.stock ?: 0
        if (cantidad in 1..stock) {
            _detalleState.update { it.copy(cantidad = cantidad) }
        }
    }

    fun onTallaChange(talla: Talla) {
        _detalleState.update { it.copy(tallaSeleccionada = talla) }
    }

    fun onColorChange(color: ColorInfo) {
        _detalleState.update { it.copy(colorSeleccionado = color) }
    }

    fun onAddToCart(carritoViewModel: CarritoViewModel) {
        val state = _detalleState.value
        val producto = state.producto ?: return

        val configuracion = ProductoConfiguracion(producto, state.tallaSeleccionada, state.colorSeleccionado, state.cantidad)
        if (configuracion.esValida()) {
            carritoViewModel.agregarProducto(configuracion.toProductoCarrito())
            _detalleState.update { it.copy(showSnackbar = true) }
        }
    }

    fun onSnackbarDismissed() {
        _detalleState.update { it.copy(showSnackbar = false) }
    }

    fun onViewCart() {
        mainViewModel.navigate(Screen.Cart.route)
    }

    fun onProductoClicked(producto: Producto) {
        mainViewModel.navigate(Screen.DetalleProducto.createRoute(producto.id))
    }
}
