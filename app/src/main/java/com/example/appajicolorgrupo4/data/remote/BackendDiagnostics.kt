package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

/** Resultado del diagnóstico del backend */
data class BackendProbeResult(
    val baseUrl: String,
    val endpoint: String,
    val httpCode: Int? = null,
    val ok: Boolean,
    val latencyMs: Long? = null,
    val message: String,
    val rawError: String? = null
)

/** Utilidades para diagnosticar la conectividad con el backend */
object BackendDiagnostics {
    private const val PRODUCTOS_ENDPOINT = "api/v1/productos"

    /** Intenta hacer un GET a /api/v1/productos para comprobar despliegue y latencia */
    suspend fun probeProductos(): BackendProbeResult = withContext(Dispatchers.IO) {
        val fullUrl = BuildConfig.BASE_URL + PRODUCTOS_ENDPOINT
        var errorBody: String? = null
        var code: Int? = null
        var success = false
        var message = ""
        val time = measureTimeMillis {
            try {
                val response = RetrofitInstance.api.getProductos()
                code = response.code()
                if (response.isSuccessful) {
                    success = true
                    message = "OK: ${response.body()?.size ?: 0} productos"
                } else {
                    errorBody = response.errorBody()?.string()?.trim()
                    message = mapError(code!!, errorBody)
                }
            } catch (e: Exception) {
                message = e.message ?: "Exception sin mensaje"
                errorBody = e.toString()
            }
        }
        BackendProbeResult(
            baseUrl = BuildConfig.BASE_URL,
            endpoint = PRODUCTOS_ENDPOINT,
            httpCode = code,
            ok = success,
            latencyMs = time,
            message = message,
            rawError = errorBody
        )
    }

    private fun mapError(code: Int, body: String?): String {
        if (body.isNullOrBlank()) return "HTTP $code sin cuerpo"
        val lower = body.lowercase()
        return when {
            lower.contains("deployment_not_found") || lower.contains("deployed not found") || lower.contains("deployment not found") ->
                "Deployment no encontrado en Vercel (HTTP $code)"
            lower.startsWith("<!doctype") || lower.startsWith("<html") ->
                when (code) {
                    502,503,504 -> "Servidor frío / gateway ($code). Reintenta."
                    else -> "Respuesta HTML inesperada ($code)"
                }
            code == 404 -> "Ruta /${PRODUCTOS_ENDPOINT} no encontrada (404)"
            code == 401 -> "No autorizado (401)"
            code == 403 -> "Acceso prohibido (403)"
            code == 429 -> "Rate limit (429)"
            code in 500..599 -> "Error interno ($code)"
            else -> body.take(200)
        }
    }
}

