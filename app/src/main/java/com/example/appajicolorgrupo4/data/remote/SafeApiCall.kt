package com.example.appajicolorgrupo4.data.remote

import java.io.IOException
import org.json.JSONObject
import retrofit2.Response

abstract class SafeApiCall {

    // Función de logging que funciona en tests y producción
    private fun log(tag: String, message: String) {
        try {
            android.util.Log.d(tag, message)
        } catch (e: RuntimeException) {
            // En tests unitarios, android.util.Log no está disponible
            println("$tag: $message")
        }
    }

    private fun logError(tag: String, message: String, throwable: Throwable? = null) {
        try {
            android.util.Log.e(tag, message, throwable)
        } catch (e: RuntimeException) {
            println("$tag: $message ${throwable?.message ?: ""}")
        }
    }

    suspend fun <T> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = call()
            log("SafeApiCall", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    log("SafeApiCall", "Success with body")
                    NetworkResult.Success(body)
                } else {
                    log("SafeApiCall", "Success but body is null")
                    NetworkResult.Error("Response body is null")
                }
            } else {
                val code = response.code()
                val rawErrorBody = response.errorBody()?.string()
                log("SafeApiCall", "Error code: $code, body length: ${rawErrorBody?.length}")
                log("SafeApiCall", "Full error body: $rawErrorBody")
                val cleaned = rawErrorBody?.trim()
                val errorMessage = buildErrorMessage(code, cleaned)
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            logError("SafeApiCall", "Exception: ${e.message}", e)
            val errorMessage =
                    when (e) {
                        is java.net.SocketTimeoutException ->
                                "El servidor está despertando, intenta de nuevo."
                        is com.google.gson.stream.MalformedJsonException ->
                                "Error técnico del servidor (Posible 502/504)."
                        is retrofit2.HttpException -> e.message() ?: "HttpException"
                        is IOException -> e.message ?: "Error de conexión (IO)"
                        else -> e.message ?: "Unknown Error"
                    }
            NetworkResult.Error(errorMessage)
        }
    }

    private fun buildErrorMessage(code: Int, body: String?): String {
        if (body.isNullOrEmpty()) return "Error code: $code"

        // HTML genérico de errores Vercel / edge
        if (body.startsWith("<!DOCTYPE html>") || body.startsWith("<html")) {
            return when (code) {
                502, 503, 504 ->
                        "El backend está despertando (código $code). Reintenta en unos segundos."
                404 -> "Recurso no encontrado (404). Verifica el endpoint."
                else -> "Error técnico del servidor (HTML $code)."
            }
        }

        // JSON sencillo
        if (body.startsWith("{") && body.endsWith("}")) {
            try {
                val json = JSONObject(body)
                // Busca claves comunes
                val messageKeys = listOf("message", "error", "detail", "msg")
                val msg =
                        messageKeys.firstNotNullOfOrNull { k ->
                            json.optString(k, null).takeIf { !it.isNullOrBlank() }
                        }
                if (!msg.isNullOrBlank()) {
                    // Mapear patrones específicos
                    return mapSpecialPatterns(code, msg)
                }
            } catch (_: Exception) {
                /* ignorar parsing fallido */
            }
        }

        // Texto plano
        return mapSpecialPatterns(code, body)
    }

    private fun mapSpecialPatterns(code: Int, msg: String): String {
        val lower = msg.lowercase()
        return when {
            // Errores de MongoDB / Base de datos
            lower.contains("mongoose") ||
                    lower.contains("mongodb") ||
                    lower.contains("buffercommands") ->
                    "Error de base de datos. El servidor está despertando, intenta de nuevo en unos segundos. ($code)"
            // Patrones de despliegue Vercel inexistente
            lower.contains("deployment_not_found") ||
                    lower.contains("deployed not found") ||
                    lower.contains("deployment not found") ->
                    "Deployment Vercel no encontrado. Verifica que la URL base sea correcta y que el proyecto esté desplegado. (${code})"
            // Patrones de not found genéricos
            code == 404 && (lower.contains("not found") || lower.contains("no encontrado")) ->
                    "Recurso no encontrado (404). Verifica ruta / api / versión."
            // Unauthorized
            code == 401 -> "No autorizado (401). Revisa credenciales o token."
            // Forbidden
            code == 403 -> "Acceso prohibido (403). Falta rol o permisos."
            // Rate limiting / cold start
            code == 429 -> "Límite de peticiones alcanzado (429). Intenta más tarde."
            // Server errors
            code in 500..599 && lower.contains("timeout") -> "Timeout interno ($code). Reintenta."
            code in 500..599 ->
                    "Error del servidor ($code). El backend está despertando, reintenta."
            else -> msg // Mensaje tal cual si no coincide
        }
    }
}
