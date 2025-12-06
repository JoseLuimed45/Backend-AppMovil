package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.local.user.UserDao
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import com.example.appajicolorgrupo4.data.remote.ResetPasswordRequest
import com.example.appajicolorgrupo4.data.remote.SafeApiCall
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) : SafeApiCall() {

    suspend fun register(nombre: String, correo: String, telefono: String, clave: String, direccion: String): NetworkResult<Long> {
        return withContext(Dispatchers.IO) {
            try {
                // Verificar si el correo ya existe
                if (userDao.getUserByEmail(correo) != null) {
                    return@withContext NetworkResult.Error("El correo ya está registrado")
                }

                // Crear y guardar el nuevo usuario
                val newUser = UserEntity(
                    nombre = nombre,
                    correo = correo,
                    telefono = telefono,
                    direccion = direccion
                )
                val userId = userDao.insert(newUser)
                NetworkResult.Success(userId)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    suspend fun login(correo: String, clave: String): NetworkResult<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                // In a real app, login should hit the API, get a token, and save the user profile locally WITHOUT password
                // For now, we assume the API login (via AuthViewModel) handles the actual authentication
                // and this local method just retrieves the user if they exist locally.
                
                val user = userDao.getUserByEmail(correo)
                if (user == null) {
                    // If not found locally, we might want to fetch from API or just return error
                    // But since we are moving to API-first auth, this local check is less relevant for auth
                    // and more for "get current user profile".
                    NetworkResult.Error("Usuario no encontrado localmente")
                } else {
                    // We NO LONGER check password locally
                    NetworkResult.Success(user)
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    suspend fun getUserById(userId: Long): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
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
        withContext(Dispatchers.IO) {
            userDao.deleteAll()
        }
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
}
