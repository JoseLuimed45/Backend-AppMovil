package com.example.appajicolorgrupo4.viewmodel

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import com.example.appajicolorgrupo4.data.AccionNotificacion
import com.example.appajicolorgrupo4.data.Notificacion
import com.example.appajicolorgrupo4.data.TipoNotificacion
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificacionesViewModel(
    private val mainViewModel: MainViewModel
) : ViewModel() {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    fun onNotificacionClicked(notificacion: Notificacion) {
        marcarComoLeida(notificacion.id)

        when (val accion = notificacion.accion) {
            is AccionNotificacion.VerPedido -> {
                mainViewModel.navigate(Screen.DetallePedido.createRoute(accion.numeroPedido))
                eliminarNotificacion(notificacion.id)
            }
            is AccionNotificacion.Navegar -> {
                mainViewModel.navigate(accion.ruta)
            }
            AccionNotificacion.Ninguna -> {
                // No se realiza ninguna acción de navegación.
            }
            null -> {
                // No se realiza ninguna acción si no hay acción definida.
            }
        }
    }

    // --- El resto de funciones no necesitan cambios ---

    fun crearNotificacionCompraExitosa(numeroPedido: String) {
        val notificacion = Notificacion(
            tipo = TipoNotificacion.COMPRA_EXITOSA,
            titulo = "¡Compra exitosa!",
            mensaje = "Su compra con el número $numeroPedido ha sido exitosa",
            numeroPedido = numeroPedido,
            accion = AccionNotificacion.VerPedido(numeroPedido)
        )
        agregarNotificacion(notificacion)
    }

    private fun agregarNotificacion(notificacion: Notificacion) {
        val notificacionesActuales = _notificaciones.value.toMutableList()
        notificacionesActuales.add(0, notificacion)
        _notificaciones.value = notificacionesActuales
    }

    fun marcarComoLeida(notificacionId: String) {
        _notificaciones.value = _notificaciones.value.map { n ->
            if (n.id == notificacionId) n.copy(leida = true) else n
        }
    }

    fun eliminarNotificacion(notificacionId: String) {
        _notificaciones.value = _notificaciones.value.filter { it.id != notificacionId }
    }

    fun marcarTodasComoLeidas() {
        _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
    }

    fun eliminarNotificacionesLeidas() {
        _notificaciones.value = _notificaciones.value.filter { !it.leida }
    }
}