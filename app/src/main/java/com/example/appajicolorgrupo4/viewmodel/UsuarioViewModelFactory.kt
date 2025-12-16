package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appajicolorgrupo4.data.repository.PedidoRepository
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager

class UsuarioViewModelFactory(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val mainViewModel: MainViewModel,
    private val pedidoRepository: PedidoRepository // Dependencia añadida
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            return UsuarioViewModel(userRepository, sessionManager, mainViewModel, pedidoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
