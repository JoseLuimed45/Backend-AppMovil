package com.example.appajicolorgrupo4.ui.state

import com.example.appajicolorgrupo4.data.local.user.UserEntity

/**
 * Representa el estado completo de la UI relacionado con el usuario.
 */
data class UsuarioUiState(
    val currentUser: UserEntity? = null,
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val isEditMode: Boolean = false,
    val updateMessage: String? = null,
    val profileImageUri: String? = null,
    val errores: UsuarioErrores = UsuarioErrores()
)

