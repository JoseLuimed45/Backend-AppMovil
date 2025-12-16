package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.google.gson.annotations.SerializedName

/** DTO para la respuesta de PUT /api/v1/usuarios/:id */
data class UpdatePerfilResponse(val token: String, val user: UserDto, val message: String? = null)

/** DTO de usuario devuelto por el backend (incluye _id y correo/email) */
data class UserDto(
        @SerializedName("_id") val id: String? = null,
        val nombre: String,
        val correo: String? = null,
        val email: String? = null,
        val telefono: String? = null,
        val direccion: String? = null,
        val rol: String? = null
) {
    fun toUserEntity(): UserEntity =
            UserEntity(
                    id = 1L,
                    mongoId = id,
                    nombre = nombre,
                    correo = correo ?: email.orEmpty(),
                    telefono = telefono.orEmpty(),
                    direccion = direccion.orEmpty()
            )
}
