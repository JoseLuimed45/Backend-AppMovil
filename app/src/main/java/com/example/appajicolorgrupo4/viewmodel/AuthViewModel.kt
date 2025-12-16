package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.domain.validation.*
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (data classes)

class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager,
    private val mainViewModel: MainViewModel
) : ViewModel() {

    // ... (estados)

    // --- LOGIN ---
    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null) }
            delay(500)

            when (val result = repository.login(s.correo.trim(), s.clave)) {
                is NetworkResult.Success -> {
                    result.data?.let { loginData ->
                        sessionManager.saveSession(loginData.user)
                        sessionManager.saveToken(loginData.token)
                        val isAdmin = loginData.rol?.uppercase() == "ADMIN" ||
                                      loginData.user.correo.contains("admin@ajicolor", ignoreCase = true)
                        sessionManager.saveIsAdmin(isAdmin)

                        _login.update { it.copy(isSubmitting = false) }

                        if (isAdmin) {
                            mainViewModel.navigate(
                                route = Screen.AdminProductos.route, // CORREGIDO
                                popUpToRoute = Screen.Login.route, // CORREGIDO
                                inclusive = true
                            )
                        } else {
                            mainViewModel.navigate(
                                route = Screen.Home.route, // CORREGIDO
                                popUpToRoute = Screen.Login.route, // CORREGIDO
                                inclusive = true
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            errorMsg = result.message ?: "Error de autenticación"
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _login.update { it.copy(isSubmitting = true) }
                }
            }
        }
    }
    
    // ... (resto de funciones de login, registro y recovery sin cambios en la navegación)
}
