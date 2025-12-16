package com.example.appajicolorgrupo4.data.remote

import android.content.Context
import android.util.Log
import com.example.appajicolorgrupo4.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/** Interceptor que agrega el token JWT a todas las peticiones HTTP */
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Obtener el token guardado
        val sessionManager = SessionManager(context)
        val token = sessionManager.getToken()

        // Si no hay token, proceder sin él (para endpoints públicos)
        if (token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "No token found, proceeding without authentication")
            return chain.proceed(originalRequest)
        }

        // Agregar el header de autenticación
        Log.d("AuthInterceptor", "Adding Authorization header with token: ${token.take(20)}...")
        val authorizedRequest =
                originalRequest.newBuilder().header("Authorization", "Bearer $token").build()

        return chain.proceed(authorizedRequest)
    }
}
