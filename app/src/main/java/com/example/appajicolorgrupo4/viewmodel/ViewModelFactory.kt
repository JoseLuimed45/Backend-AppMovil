package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import com.example.appajicolorgrupo4.data.repository.RemotePedidoRepository

/**
 * Factory dedicada para crear PedidosViewModel con todas sus dependencias inyectadas.
 * Este patrón hace que las dependencias sean explícitas y el ViewModel más testeable.
 */
class PedidosViewModelFactory(
    private val mainViewModel: MainViewModel,
    private val carritoViewModel: CarritoViewModel,
    private val usuarioViewModel: UsuarioViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)) {
            // Inyectar todas las dependencias requeridas en el constructor
            return PedidosViewModel(
                pedidoRepository = RemotePedidoRepository(RetrofitInstance.api),
                mainViewModel = mainViewModel,
                carritoViewModel = carritoViewModel,
                usuarioViewModel = usuarioViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
