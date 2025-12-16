package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.GeneradorNumeroPedido
import com.example.appajicolorgrupo4.data.MetodoPago
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import com.example.appajicolorgrupo4.data.repository.RemotePedidoRepository
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PedidosViewModel(
    private val pedidoRepository: RemotePedidoRepository = RemotePedidoRepository(RetrofitInstance.api),
    private val mainViewModel: MainViewModel,
    private val carritoViewModel: CarritoViewModel,
    private val usuarioViewModel: UsuarioViewModel
) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<PedidoCompleto>>(emptyList())
    val pedidos: StateFlow<List<PedidoCompleto>> = _pedidos.asStateFlow()

    private val _ultimoPedidoGuardado = MutableStateFlow<String?>(null)
    val ultimoPedidoGuardado: StateFlow<String?> = _ultimoPedidoGuardado.asStateFlow()

    fun confirmarCompraFicticia(
        metodoPago: MetodoPago,
        direccionEnvio: String,
        telefono: String,
        notasAdicionales: String
    ) {
        viewModelScope.launch {
            val user = usuarioViewModel.currentUser.value
            val productos = carritoViewModel.productos.value
            if (user == null || productos.isEmpty()) {
                return@launch
            }

            val numeroPedido = GeneradorNumeroPedido.generar(user.nombre)
            val pedido = PedidoCompleto(
                numeroPedido = numeroPedido,
                nombreUsuario = user.nombre,
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

            val resultado = pedidoRepository.guardarPedido(pedido, user.id.toString())
            resultado.onSuccess { numPedido ->
                carritoViewModel.limpiarCarrito()

                // CORRECCIÓN DEFINITIVA: Ambos parámetros de ruta son ahora Strings.
                mainViewModel.navigate(
                    route = Screen.Success.createRoute(numPedido),
                    popUpToRoute = Screen.Cart.route,
                    inclusive = true
                )

                val pedidosActuales = _pedidos.value.toMutableList()
                pedidosActuales.add(0, pedido)
                _pedidos.value = pedidosActuales
                _ultimoPedidoGuardado.value = numPedido
            }.onFailure { error ->
                android.util.Log.e("PedidosViewModel", "Error al guardar pedido: ${error.message}")
            }
        }
    }
    // ... (resto de funciones)
}
