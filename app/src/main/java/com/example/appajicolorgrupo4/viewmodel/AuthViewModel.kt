package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appajicolorgrupo4.domain.validation.*
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import android.util.Log

// ----------------- ESTADOS DE UI (observable con StateFlow) -----------------

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val correoError: String? = null,
    val claveError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val isAdmin: Boolean = false,
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
    val direccionError: String? = null,
    val telefonoError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

// ----------------- VIEWMODEL -----------------

class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Flujos de estado para observar desde la UI
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // ----------------- LOGIN: handlers y envío -----------------

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
        val can = s.correoError == null &&
                s.correo.isNotBlank() &&
                s.clave.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)

            // HARDCODED ADMIN CHECK
            if (s.correo.trim() == "admin@ajicolor.cl" && s.clave == "ajicolor") {
                Log.d("AuthVM", "Login admin detectado")
                // Create a fake admin user session
                val adminUser = com.example.appajicolorgrupo4.data.local.user.UserEntity(
                    id = 9999,
                    nombre = "Administrador",
                    correo = "admin@ajicolor.cl",
                    telefono = "000000000",
                    direccion = "Oficina Central"
                )
                sessionManager.saveSession(adminUser)
                sessionManager.saveIsAdmin(true)
                // We can use a special flag or just rely on the email in the UI to navigate
                _login.update { it.copy(isSubmitting = false, success = true, isAdmin = true, errorMsg = null) }
                return@launch
            }

            when (val result = repository.login(s.correo.trim(), s.clave)) {
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Success -> {
                    // Guardar la sesión del usuario
                    result.data?.let { user ->
                        sessionManager.saveSession(user)
                    }
                    sessionManager.saveIsAdmin(false)
                    _login.update { it.copy(isSubmitting = false, success = true, isAdmin = false, errorMsg = null) }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Error -> {
                    _login.update { it.copy(isSubmitting = false, success = false, isAdmin = false,
                        errorMsg = result.message ?: "Error de autenticación") }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Loading -> {
                    _login.update { it.copy(isSubmitting = true) }
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(nombre = filtered, nombreError = validateNameLettersOnly(filtered))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(correo = value, correoError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onDireccionChange(value: String) {
        _register.update {
            it.copy(direccion = value, direccionError = validateDireccion(value))
        }
        recomputeRegisterCanSubmit()
    }

    fun onTelefonoChange(value: String) {
        // Filtrar solo números
        val soloNumeros = value.filter { it.isDigit() }
        _register.update {
            it.copy(telefono = soloNumeros, telefonoError = validatePhoneDigitsOnly(soloNumeros))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(clave = value, claveError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.clave, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.clave, value)) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nombreError, s.correoError, s.direccionError, s.telefonoError, s.claveError, s.confirmError).all { it == null }
        val filled = s.nombre.isNotBlank() && s.correo.isNotBlank() && s.direccion.isNotBlank() && s.telefono.isNotBlank() && s.clave.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)

            when (val result = repository.register(
                nombre = s.nombre.trim(),
                correo = s.correo.trim(),
                telefono = s.telefono.trim(),
                clave = s.clave,
                direccion = s.direccion.trim()
            )) {
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Success -> {
                    _register.update { it.copy(isSubmitting = false, success = true, errorMsg = null) }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Error -> {
                    _register.update { it.copy(isSubmitting = false, success = false,
                        errorMsg = result.message ?: "No se pudo registrar") }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Loading -> {
                    _register.update { it.copy(isSubmitting = true) }
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- PASSWORD RECOVERY -----------------

    private val _recoverState = MutableStateFlow(RecoverUiState())
    val recoverState: StateFlow<RecoverUiState> = _recoverState

    private val _resetState = MutableStateFlow(ResetUiState())
    val resetState: StateFlow<ResetUiState> = _resetState

    fun onRecoverEmailChange(value: String) {
        _recoverState.update { it.copy(email = value, emailError = validateEmail(value)) }
    }

    fun submitRecover() {
        val s = _recoverState.value
        if (s.emailError != null || s.email.isBlank() || s.isLoading) return

        viewModelScope.launch {
            _recoverState.update { it.copy(isLoading = true, error = null, success = false) }
            delay(500)
            
            when (val result = repository.recoverPassword(s.email.trim())) {
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Success -> {
                    _recoverState.update { it.copy(isLoading = false, success = true, error = null) }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Error -> {
                    _recoverState.update { it.copy(isLoading = false, success = false, error = result.message) }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Loading -> {
                    _recoverState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun clearRecoverState() {
        _recoverState.update { RecoverUiState() }
    }

    // ----------------- RESET PASSWORD -----------------

    fun onResetCodeChange(value: String) {
        _resetState.update { it.copy(code = value) }
    }

    fun onResetPassChange(value: String) {
        _resetState.update { it.copy(newPassword = value, passError = validateStrongPassword(value)) }
    }

    fun onResetConfirmChange(value: String) {
        _resetState.update { it.copy(confirmPassword = value, confirmError = validateConfirm(it.newPassword, value)) }
    }

    fun submitReset() {
        val s = _resetState.value
        val noErrors = s.passError == null && s.confirmError == null
        val filled = s.code.isNotBlank() && s.newPassword.isNotBlank() && s.confirmPassword.isNotBlank()

        if (!noErrors || !filled || s.isLoading) return

        viewModelScope.launch {
            _resetState.update { it.copy(isLoading = true, error = null, success = false) }
            delay(500)
            // We need the email from the recover state or passed as argument. 
            // Assuming the user stays in the flow, we can use recoverState.email
            val email = _recoverState.value.email
            
            when (val result = repository.resetPassword(email, s.code.trim(), s.newPassword)) {
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Success -> {
                    _resetState.update { it.copy(isLoading = false, success = true, error = null) }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Error -> {
                    _resetState.update { it.copy(isLoading = false, success = false, error = result.message) }
                }
                is com.example.appajicolorgrupo4.data.remote.NetworkResult.Loading -> {
                    _resetState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun clearResetState() {
        _resetState.update { ResetUiState() }
    }
}

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
