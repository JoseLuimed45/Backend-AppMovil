package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.repository.PedidoRepository
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.state.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager,
    private val mainViewModel: MainViewModel,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    fun onNombreChange(valor: String) = _uiState.update { it.copy(nombre = valor) }
    fun onCorreoChange(valor: String) = _uiState.update { it.copy(correo = valor) }
    fun onTelefonoChange(valor: String) = _uiState.update { it.copy(telefono = valor.filter { it.isDigit() }) }
    fun onDireccionChange(valor: String) = _uiState.update { it.copy(direccion = valor) }

    fun cargarPerfil() {
        viewModelScope.launch {
            sessionManager.getCurrentUser()?.let { user ->
                _uiState.update { currentState ->
                    currentState.copy(
                        currentUser = user,
                        nombre = user.nombre,
                        correo = user.correo,
                        telefono = user.telefono,
                        direccion = user.direccion,
                        profileImageUri = sessionManager.getProfileImageUri()
                    )
                }
            }
        }
    }

    fun activarEdicion() = _uiState.update { it.copy(isEditMode = true) }

    fun cancelarEdicion() {
        _uiState.update { it.copy(isEditMode = false) }
        cargarPerfil() // Restaura los datos originales desde la sesión
    }

    fun guardarPerfil() {
        val currentState = _uiState.value
        val mongoId = currentState.currentUser?.mongoId ?: return

        viewModelScope.launch {
            when (val resultado = repository.updatePerfil(mongoId, currentState.nombre, currentState.telefono, currentState.direccion)) {
                is NetworkResult.Success -> {
                    resultado.data?.let {
                        sessionManager.saveSession(it.user)
                        sessionManager.saveToken(it.token)
                        cargarPerfil() // Recarga el perfil con los nuevos datos
                        _uiState.update { state -> state.copy(isEditMode = false, updateMessage = "Perfil actualizado con éxito") }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(updateMessage = resultado.message ?: "Error al actualizar") }
                }
                else -> {
                    // Opcional: manejar estado de carga
                }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiState.value = UsuarioUiState() // Resetea completamente el estado de la UI
            // Navega a la pantalla de inicio y limpia toda la pila de navegación anterior
            mainViewModel.navigate(Screen.StartScreen.route, popUpToRoute = Screen.Home.route, inclusive = true)
        }
    }

    fun guardarFotoPerfil(uri: String?) {
        sessionManager.saveProfileImageUri(uri)
        _uiState.update { it.copy(profileImageUri = uri) }
    }

    fun limpiarMensajeActualizacion() {
        _uiState.update { it.copy(updateMessage = null) }
    }

    fun clearAllData(onFinished: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllUsers()
            pedidoRepository.deleteAllPedidos()
            sessionManager.clearSession()
            _uiState.value = UsuarioUiState()
            onFinished()
        }
    }
}