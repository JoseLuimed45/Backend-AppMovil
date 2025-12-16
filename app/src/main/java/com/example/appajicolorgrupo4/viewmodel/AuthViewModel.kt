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

// --- DATA CLASSES PARA LOS ESTADOS DE UI ---

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val correoError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val confirm: String = "",
    val nombreError: String? = null,
    val correoError: String? = null,
    val claveError: String? = null,
    val confirmError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMsg: String? = null
)

data class RecoverUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

data class ResetUiState(
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passError: String? = null,
    val confirmError: String? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

// --- VIEWMODEL ---

class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager,
    private val mainViewModel: MainViewModel
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register.asStateFlow()

    private val _recoverState = MutableStateFlow(RecoverUiState())
    val recoverState: StateFlow<RecoverUiState> = _recoverState.asStateFlow()

    private val _resetState = MutableStateFlow(ResetUiState())
    val resetState: StateFlow<ResetUiState> = _resetState.asStateFlow()

    // --- LÓGICA DE LOGIN ---

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(correo = value, correoError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(clave = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.correoError == null && s.correo.isNotBlank() && s.clave.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null) }
            when (val result = repository.login(s.correo.trim(), s.clave)) {
                is NetworkResult.Success -> {
                    result.data?.let {
                        sessionManager.saveSession(it.user)
                        sessionManager.saveToken(it.token)
                        val isAdmin = it.rol?.uppercase() == "ADMIN"
                        sessionManager.saveIsAdmin(isAdmin)
                        _login.update { state -> state.copy(isSubmitting = false) }
                        if (isAdmin) {
                            mainViewModel.navigate(Screen.AdminProductos.route, popUpToRoute = Screen.Login.route, inclusive = true)
                        } else {
                            mainViewModel.navigate(Screen.Home.route, popUpToRoute = Screen.Login.route, inclusive = true)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _login.update { it.copy(isSubmitting = false, errorMsg = result.message) }
                }
                else -> _login.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun clearLoginResult() = _login.update { it.copy(errorMsg = null) }

    // --- LÓGICA DE REGISTRO ---

    fun onNameChange(value: String) {
        _register.update { it.copy(nombre = value, nombreError = validateNameLettersOnly(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(correo = value, correoError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onDireccionChange(value: String) {
        _register.update { it.copy(direccion = value) }
    }

    fun onTelefonoChange(value: String) {
        _register.update { it.copy(telefono = value.filter { it.isDigit() }) }
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(clave = value, claveError = validateStrongPassword(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(_register.value.clave, value)) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val hasNoErrors = listOf(s.nombreError, s.correoError, s.claveError, s.confirmError).all { it == null }
        val areFieldsFilled = s.nombre.isNotBlank() && s.correo.isNotBlank() && s.clave.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = hasNoErrors && areFieldsFilled) }
    }

    fun submitRegister() {
        if (!_register.value.canSubmit || _register.value.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null) }
            val s = _register.value
            val result = repository.register(s.nombre, s.correo, s.telefono, s.clave, s.direccion)
            if (result is NetworkResult.Success) {
                mainViewModel.navigate(Screen.Login.route, popUpToRoute = Screen.Registro.route, inclusive = true)
            } else if (result is NetworkResult.Error) {
                _register.update { it.copy(isSubmitting = false, errorMsg = result.message) }
            }
        }
    }

    fun clearRegisterResult() = _register.update { it.copy(errorMsg = null) }

    // --- LÓGICA DE RECUPERACIÓN DE CONTRASEÑA ---

    fun onRecoverEmailChange(value: String) {
        _recoverState.update { it.copy(email = value, emailError = validateEmail(value)) }
    }

    fun submitRecover() {
        val s = _recoverState.value
        if (s.emailError != null || s.email.isBlank() || s.isLoading) return
        viewModelScope.launch {
            _recoverState.update { it.copy(isLoading = true, error = null, success = false) }
            when (repository.recoverPassword(s.email.trim())) {
                is NetworkResult.Success -> _recoverState.update { it.copy(isLoading = false, success = true) }
                is NetworkResult.Error -> _recoverState.update { it.copy(isLoading = false, error = "Email no encontrado.") }
                else -> { /* No-op */ }
            }
        }
    }

    fun clearRecoverResult() = _recoverState.update { it.copy(success = false, error = null) }

    fun onResetCodeChange(value: String) = _resetState.update { it.copy(code = value) }

    fun onResetPassChange(value: String) {
        _resetState.update { it.copy(newPassword = value, passError = validateStrongPassword(value)) }
    }

    fun onResetConfirmChange(value: String) {
        _resetState.update { it.copy(confirmPassword = value, confirmError = validateConfirm(_resetState.value.newPassword, value)) }
    }

    fun submitReset() {
        val s = _resetState.value
        val canSubmit = s.passError == null && s.confirmError == null && s.code.isNotBlank() && s.newPassword.isNotBlank()
        if (!canSubmit || s.isLoading) return

        viewModelScope.launch {
            _resetState.update { it.copy(isLoading = true, error = null, success = false) }
            val email = _recoverState.value.email
            when (repository.resetPassword(email, s.code, s.newPassword)) {
                is NetworkResult.Success -> {
                    _resetState.update { it.copy(isLoading = false, success = true) }
                    mainViewModel.navigateBack()
                }
                is NetworkResult.Error -> _resetState.update { it.copy(isLoading = false, error = "Código inválido o expirado.") }
                else -> { /* No-op */ }
            }
        }
    }

    /**
     * Limpia el estado de la pantalla de reseteo de contraseña.
     */
    fun clearResetState() {
        _resetState.update { ResetUiState() }
    }
}
