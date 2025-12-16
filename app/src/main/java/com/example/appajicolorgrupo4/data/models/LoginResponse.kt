package com.example.appajicolorgrupo4.data.models

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint POST /api/v1/usuarios/login Contiene los datos del usuario y el token JWT
 */
data class LoginResponse(
    @SerializedName("_id") val id: String? = null,
    val nombre: String?, // Hecho nullable para aceptar respuestas sin este campo
    val email: String,
    val telefono: String? = "",
    val direccion: String? = "",
    val token: String,
    val rol: String? = null
) {
    /** 
     * Convierte la respuesta de login a UserEntity. 
     * Si el nombre es nulo o está vacío, usa la parte del email antes del '@' como fallback.
     */
    fun toUserEntity(): UserEntity {
        val nombreDeRespaldo = email.split('@').firstOrNull() ?: "Usuario"
        return UserEntity(
            id = 1L, // ID local para Room (no se usa en backend)
            mongoId = id, // ObjectId de MongoDB (_id del backend)
            nombre = if (nombre.isNullOrBlank()) nombreDeRespaldo else nombre,
            correo = email,
            telefono = telefono ?: "",
            direccion = direccion ?: ""
        )
    }
}
