package com.example.appajicolorgrupo4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.data.local.database.AppDatabase
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.repository.PedidoRepository
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.ui.state.UsuarioErrores
import com.example.appajicolorgrupo4.ui.state.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val pedidoRepository: PedidoRepository
    private val sessionManager: SessionManager

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()

    private val _registroResultado = MutableStateFlow<String?>(null)
    val registroResultado: StateFlow<String?> = _registroResultado.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _updateResultado = MutableStateFlow<String?>(null)
    val updateResultado: StateFlow<String?> = _updateResultado.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = UserRepository(database.userDao())
        pedidoRepository = PedidoRepository(database.pedidoDao())
        sessionManager = SessionManager(application)
        cargarPerfil()
    }

    fun actualizaNombre(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun actualizaCorreo(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun actualizaTelefono(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }
        _estado.update {
            it.copy(telefono = soloNumeros, errores = it.errores.copy(telefono = null))
        }
    }

    fun actualizaClave(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun actualizaConfirmarClave(valor: String) {
        _estado.update {
            it.copy(confirmarClave = valor, errores = it.errores.copy(confirmarClave = null))
        }
    }

    fun actualizaDireccion(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun actualizaAceptaTerminos(valor: Boolean) {
        _estado.update {
            it.copy(aceptaTerminos = valor, errores = it.errores.copy(aceptaTerminos = null))
        }
    }

    // NOTA: Registro delegado a AuthViewModel para evitar duplicidad y conflictos

    fun cargarPerfil() {
        viewModelScope.launch {
            android.util.Log.d(
                    "UsuarioViewModel",
                    "cargarPerfil(): isLoggedIn=${sessionManager.isLoggedIn()}"
            )
            val user = sessionManager.getCurrentUser()
            android.util.Log.d("UsuarioViewModel", "cargarPerfil(): user=${user}")
            if (user != null) {
                _currentUser.value = user
                _estado.update {
                    it.copy(
                            nombre = user.nombre,
                            correo = user.correo,
                            telefono = user.telefono,
                            direccion = user.direccion
                    )
                }
                _profileImageUri.value = sessionManager.getProfileImageUri()
            } else {
                android.util.Log.w("UsuarioViewModel", "No hay usuario en sesión")
            }
        }
    }

    fun saveSession(user: UserEntity) {
        android.util.Log.d(
                "UsuarioViewModel",
                "saveSession(): saving user id=${user.id}, correo=${user.correo}"
        )
        sessionManager.saveSession(user)
        _currentUser.value = user
        cargarPerfil()
    }

    fun activarEdicion() {
        _isEditMode.value = true
    }

    fun cancelarEdicion() {
        _isEditMode.value = false
        cargarPerfil()
    }

    /**
     * @deprecated Usar guardarPerfil() en su lugar. Esta función solo actualiza Room/local y NO
     * sincroniza con MongoDB. Use guardarPerfil() que hace PUT al backend y sincroniza
     * correctamente.
     */
    @Deprecated(
            message = "Usar guardarPerfil() para actualizar perfil (sincroniza con backend)",
            replaceWith = ReplaceWith("guardarPerfil()"),
            level = DeprecationLevel.WARNING
    )
    // NOTA: Actualización sincronizada con backend en guardarPerfil() - usar esa versión para
    // persistencia

    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun limpiarMensajeActualizacion() {
        _updateResultado.value = null
    }

    fun guardarFotoPerfil(uri: String?) {
        _profileImageUri.value = uri
        sessionManager.saveProfileImageUri(uri)
    }

    fun guardarPerfil() {
        // Para edición de perfil, validamos solo nombre/correo/telefono/direccion
        if (!validarFormularioPerfil()) {
            return
        }

        val estadoActual = _estado.value
        val usuario = _currentUser.value

        if (usuario == null) {
            _updateResultado.value = "No hay usuario autenticado"
            return
        }

        // Usar mongoId (ObjectId del backend) en lugar del id local
        val mongoId = usuario.mongoId ?: sessionManager.getMongoId()

        if (mongoId == null) {
            _updateResultado.value =
                    "Error: ID del backend no disponible. Por favor, inicie sesión de nuevo."
            android.util.Log.e("UsuarioViewModel", "mongoId es null, no se puede hacer PUT")
            return
        }

        viewModelScope.launch {
            try {
                val resultado =
                        repository.updatePerfil(
                                userId = mongoId, // Usar ObjectId de MongoDB
                                nombre = estadoActual.nombre,
                                telefono = estadoActual.telefono,
                                direccion = estadoActual.direccion
                        )

                when (resultado) {
                    is NetworkResult.Success -> {
                        val data = resultado.data
                        if (data == null) {
                            _updateResultado.value = "Error: sin datos del servidor"
                            android.util.Log.e(
                                    "UsuarioViewModel",
                                    "resultado.data es nulo en Success"
                            )
                            return@launch
                        }

                        val userResp = data.user
                        val newToken = data.token

                        _updateResultado.value = "Perfil actualizado exitosamente"
                        _estado.update {
                            it.copy(
                                    nombre = userResp.nombre,
                                    correo = userResp.correo,
                                    telefono = userResp.telefono,
                                    direccion = userResp.direccion
                            )
                        }
                        // Preservar id local si el backend no devuelve el id esperado
                        val usuarioLocal = _currentUser.value
                        val usuarioActualizado =
                                if (usuarioLocal != null && userResp.id <= 0L) {
                                    userResp.copy(id = usuarioLocal.id)
                                } else {
                                    userResp
                                }
                        _currentUser.value = usuarioActualizado
                        sessionManager.saveSession(usuarioActualizado)

                        // Guardar el nuevo token recibido del servidor
                        sessionManager.saveToken(newToken)
                        android.util.Log.d(
                                "UsuarioViewModel",
                                "Perfil actualizado: ${usuarioActualizado.correo}, token renovado"
                        )

                        _isEditMode.value = false
                    }
                    is NetworkResult.Error -> {
                        _updateResultado.value = resultado.message ?: "Error al actualizar perfil"
                        android.util.Log.e("UsuarioViewModel", "Update error: ${resultado.message}")
                    }
                    is NetworkResult.Loading -> {
                        // No action needed
                    }
                }
            } catch (e: Exception) {
                _updateResultado.value = "Error: ${e.message}"
                android.util.Log.e("UsuarioViewModel", "Exception updating profile: ${e.message}")
            }
        }
    }

    fun cerrarSesion() {
        android.util.Log.d("UsuarioViewModel", "cerrarSesion(): clearing session")
        sessionManager.clearSession()
        _currentUser.value = null
        _estado.update { UsuarioUiState() }
        _isEditMode.value = false
        _profileImageUri.value = null
    }

    fun eliminarFotoPerfil() {
        _profileImageUri.value = null
        sessionManager.clearProfileImage()
    }

    fun clearAllData(onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteAllUsers()
            pedidoRepository.deleteAllPedidos()
            sessionManager.clearSession()
            _currentUser.value = null
            _estado.update { UsuarioUiState() }
            onFinished()
        }
    }

    private fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        var valido = true
        val errores =
                UsuarioErrores()
                        .copy(
                                nombre =
                                        if (estadoActual.nombre.isBlank())
                                                "El nombre es obligatorio"
                                        else null,
                                correo =
                                        if (estadoActual.correo.isBlank() ||
                                                        !estadoActual.correo.contains("@")
                                        )
                                                "Correo inválido"
                                        else null,
                                telefono =
                                        if (estadoActual.telefono.isBlank())
                                                "El teléfono es obligatorio"
                                        else if (estadoActual.telefono.length < 9)
                                                "El teléfono debe tener al menos 8 dígitos"
                                        else null,
                                clave =
                                        if (estadoActual.clave.length < 6)
                                                "La clave debe tener al menos 6 caracteres"
                                        else null,
                                confirmarClave =
                                        if (estadoActual.confirmarClave.isBlank())
                                                "Debe confirmar la contraseña"
                                        else if (estadoActual.clave != estadoActual.confirmarClave)
                                                "Las contraseñas no coinciden"
                                        else null,
                                aceptaTerminos =
                                        if (!estadoActual.aceptaTerminos)
                                                "Debes aceptar los términos"
                                        else null
                        )

        if (errores.hayErrores) {
            valido = false
        }

        _estado.update { it.copy(errores = errores) }
        return valido
    }

    private fun validarFormularioPerfil(): Boolean {
        val estadoActual = _estado.value
        var valido = true
        val errores =
                UsuarioErrores()
                        .copy(
                                nombre =
                                        if (estadoActual.nombre.isBlank())
                                                "El nombre es obligatorio"
                                        else null,
                                correo =
                                        if (estadoActual.correo.isBlank() ||
                                                        !estadoActual.correo.contains("@")
                                        )
                                                "Correo inválido"
                                        else null,
                                telefono =
                                        if (estadoActual.telefono.isBlank())
                                                "El teléfono es obligatorio"
                                        else if (estadoActual.telefono.length < 8)
                                                "El teléfono debe tener al menos 8 dígitos"
                                        else null,
                                direccion =
                                        if (estadoActual.direccion.isBlank())
                                                "La dirección es obligatoria"
                                        else null
                        )

        if (errores.hayErrores) {
            valido = false
        }

        _estado.update { it.copy(errores = errores) }
        return valido
    }
}
