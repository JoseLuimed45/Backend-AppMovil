package com.example.appajicolorgrupo4.data.remote

import retrofit2.Response
import java.io.IOException

abstract class SafeApiCall {
    suspend fun <T> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error("Response body is null")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody.isNullOrEmpty()) {
                    "Error code: ${response.code()}"
                } else {
                    // Check if error body is HTML (Vercel error)
                    if (errorBody.trim().startsWith("<!DOCTYPE html>") || errorBody.trim().startsWith("<html")) {
                        "Error técnico del servidor (Posible 502/504)."
                    } else {
                        errorBody
                    }
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is java.net.SocketTimeoutException -> "El servidor está despertando, intenta de nuevo."
                is com.google.gson.stream.MalformedJsonException -> "Error técnico del servidor (Posible 502/504)."
                is retrofit2.HttpException -> e.message() ?: "HttpException"
                else -> e.message ?: "Unknown Error"
            }
            NetworkResult.Error(errorMessage)
        }
    }
}
