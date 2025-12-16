package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager

/**
 * Factory para crear AuthViewModel con sus dependencias inyectadas.
 */
class AuthViewModelFactory(
    private val repository: UserRepository,
    private val sessionManager: SessionManager,
    private val mainViewModel: MainViewModel // Dependencia añadida
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Pasar la nueva dependencia al constructor de AuthViewModel
            return AuthViewModel(repository, sessionManager, mainViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
