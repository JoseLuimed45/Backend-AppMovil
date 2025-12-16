package com.example.appajicolorgrupo4.viewmodel

import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.GeneradorNumeroPedido
import com.example.appajicolorgrupo4.data.MetodoPago
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.data.repository.RemotePedidoRepository
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PedidosViewModel(
    private val mainViewModel: MainViewModel,
    private val carritoViewModel: CarritoViewModel,
    private val usuarioViewModel: UsuarioViewModel,
    private val pedidoRepository: RemotePedidoRepository
) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<PedidoCompleto>>(emptyList())
    val pedidos: StateFlow<List<PedidoCompleto>> = _pedidos.asStateFlow()

    fun confirmarCompraFicticia(
        metodoPago: MetodoPago,
        direccionEnvio: String,
        telefono: String,
        notasAdicionales: String
    ) {
        viewModelScope.launch {
            // CORRECCIÓN: Obtiene el usuario desde el uiState del UsuarioViewModel
            val user = usuarioViewModel.uiState.value.currentUser
            val productos = carritoViewModel.productos.value
            if (user == null || user.mongoId == null || productos.isEmpty()) {
                // No se puede proceder sin un usuario con ID de mongo o sin productos
                return@launch
            }

            val numeroPedido = GeneradorNumeroPedido.generar(user.nombre)
            val pedido = PedidoCompleto(
                numeroPedido = numeroPedido,
                nombreUsuario = user.nombre, // CORRECCIÓN
                productos = productos,
                subtotal = carritoViewModel.subtotal.value.toDouble(),
                impuestos = carritoViewModel.iva.value.toDouble(),
                costoEnvio = carritoViewModel.costoEnvio.value.toDouble(),
                total = carritoViewModel.total.value.toDouble(),
                direccionEnvio = direccionEnvio,
                telefono = telefono,
                notasAdicionales = notasAdicionales,
                metodoPago = metodoPago,
                estado = EstadoPedido.CONFIRMADO,
                fechaCreacion = System.currentTimeMillis(),
                fechaConfirmacion = System.currentTimeMillis()
            )

            // Usamos el mongoId del usuario para guardar el pedido
            val resultado = pedidoRepository.guardarPedido(pedido, user.mongoId)
            resultado.onSuccess { numPedido ->
                carritoViewModel.limpiarCarrito()
                mainViewModel.navigate(
                    route = Screen.Success.createRoute(numPedido),
                    popUpToRoute = Screen.Cart.route,
                    inclusive = true
                )
            }
        }
    }

    fun cargarPedidosUsuario(userId: String) {
        viewModelScope.launch {
            pedidoRepository.obtenerPedidosUsuario(userId).collect { pedidos ->
                _pedidos.value = pedidos
            }
        }
    }
}