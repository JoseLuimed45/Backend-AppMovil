package com.example.appajicolorgrupo4.data.remote

/** Resultado del login que contiene el usuario y el token JWT */
data class LoginData(
        val user: com.example.appajicolorgrupo4.data.local.user.UserEntity,
        val token: String,
        val rol: String? = null // Rol del usuario (ADMIN o USER)
)
