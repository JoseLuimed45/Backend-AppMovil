package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.local.user.UserDao
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.remote.LoginData
import com.example.appajicolorgrupo4.data.remote.LoginRequest
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.remote.RegisterRequest
import com.example.appajicolorgrupo4.data.remote.ResetPasswordRequest
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import com.example.appajicolorgrupo4.data.remote.SafeApiCall
import com.example.appajicolorgrupo4.data.remote.UpdatePerfilRequest
import com.example.appajicolorgrupo4.data.remote.UpdatePerfilResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) : SafeApiCall() {

    suspend fun register(
            nombre: String,
            correo: String,
            telefono: String,
            clave: String,
            direccion: String
    ): NetworkResult<Long> {
        return withContext(Dispatchers.IO) {
            try {
                // Crear request para el backend
                val registerRequest =
                        RegisterRequest(
                                nombre = nombre,
                                email = correo,
                                password = clave,
                                telefono = telefono.ifEmpty { null },
                                direccion = direccion.ifEmpty { null }
                        )

                // Hacer registro contra la API del backend
                val response = RetrofitInstance.api.register(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    var userEntity = response.body()!!
                    // Guardar también en DB local
                    userEntity = userEntity.copy(id = 1L) // Asegurar ID consistente para sesión
                    val userId = userDao.insert(userEntity)
                    android.util.Log.d("UserRepository", "Registration successful: $correo")
                    NetworkResult.Success(userId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage =
                            try {
                                val json = org.json.JSONObject(errorBody ?: "{}")
                                json.optString("message", "Error en el registro")
                            } catch (e: Exception) {
                                "Error en el registro"
                            }
                    android.util.Log.e("UserRepository", "Registration failed: $errorMessage")
                    NetworkResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Register exception: ${e.message}")
                NetworkResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    suspend fun login(correo: String, clave: String): NetworkResult<LoginData> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("UserRepository", "Login attempt for: $correo")

                // Hacer login contra la API del backend
                val loginRequest = LoginRequest(email = correo, password = clave)
                val response = RetrofitInstance.api.login(loginRequest)

                android.util.Log.d("UserRepository", "Login response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    android.util.Log.d("UserRepository", "Login successful: ${loginResponse.email}")

                    // Convertir la respuesta a UserEntity y retornar con el token y rol
                    val userEntity = loginResponse.toUserEntity()
                    val loginData =
                            LoginData(
                                    user = userEntity,
                                    token = loginResponse.token,
                                    rol = loginResponse.rol // Incluir el rol del backend
                            )

                    NetworkResult.Success(loginData)
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e(
                            "UserRepository",
                            "Login failed. Code: ${response.code()}, Body: $errorBody"
                    )

                    val errorMessage =
                            try {
                                val json = org.json.JSONObject(errorBody ?: "{}")
                                json.optString("message", "Error en el login")
                            } catch (e: Exception) {
                                errorBody ?: "Error en el login"
                            }

                    NetworkResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Login exception: ${e.message}", e)
                NetworkResult.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun getUserById(userId: Long): UserEntity? {
        return withContext(Dispatchers.IO) { userDao.getUserById(userId) }
    }

    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                userDao.update(user)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteAllUsers() {
        withContext(Dispatchers.IO) { userDao.deleteAll() }
    }

    suspend fun recoverPassword(email: String): NetworkResult<String> {
        return safeApiCall {
            val response = RetrofitInstance.api.recoverPassword(mapOf("email" to email))
            // Map the response to just the message string if successful
            if (response.isSuccessful) {
                retrofit2.Response.success(response.body()?.message ?: "Código enviado")
            } else {
                retrofit2.Response.error(response.code(), response.errorBody()!!)
            }
        }
    }

    suspend fun resetPassword(email: String, code: String, newPass: String): NetworkResult<String> {
        return safeApiCall {
            val request = ResetPasswordRequest(email, code, newPass)
            val response = RetrofitInstance.api.resetPassword(request)
            if (response.isSuccessful) {
                retrofit2.Response.success(response.body()?.token ?: "")
            } else {
                retrofit2.Response.error(response.code(), response.errorBody()!!)
            }
        }
    }

    suspend fun updatePerfil(
            userId: String,
            nombre: String,
            telefono: String,
            direccion: String
    ): NetworkResult<LoginData> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("UserRepository", "Updating profile for: $userId")

                val perfilRequest =
                        UpdatePerfilRequest(
                                nombre = nombre,
                                telefono = telefono,
                                direccion = direccion
                        )
                val response = RetrofitInstance.api.updatePerfil(userId, perfilRequest)

                if (response.isSuccessful && response.body() != null) {
                    val updateResponse: UpdatePerfilResponse = response.body()!!

                    val userEntity = updateResponse.user.toUserEntity()
                    val loginData =
                            LoginData(
                                    user = userEntity,
                                    token = updateResponse.token,
                                    rol = updateResponse.user.rol
                            )

                    // Actualizar en DB local también
                    val updateUser = userEntity.copy(id = 1L)
                    userDao.insert(updateUser)

                    android.util.Log.d("UserRepository", "Profile updated successfully")
                    NetworkResult.Success(loginData)
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("UserRepository", "Update failed: $errorBody")
                    val errorMessage =
                            try {
                                val json = org.json.JSONObject(errorBody ?: "{}")
                                json.optString("message", "Error al actualizar perfil")
                            } catch (e: Exception) {
                                "Error al actualizar perfil"
                            }
                    NetworkResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Update exception: ${e.message}")
                NetworkResult.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
