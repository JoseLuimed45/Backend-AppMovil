package com.example.appajicolorgrupo4.data.remote

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class SafeApiCallTest : SafeApiCall() {

    @Test
    fun `safeApiCall should return Success when API call succeeds`() = runTest {
        // Given
        val expectedData = "Success data"
        val mockCall: suspend () -> Response<String> = { Response.success(expectedData) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedData, (result as NetworkResult.Success).data)
    }

    @Test
    fun `safeApiCall should return Error when response body is null`() = runTest {
        // Given
        val mockCall: suspend () -> Response<String?> = { Response.success(null) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Response body is null", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should return Error with error code when API fails`() = runTest {
        // Given
        val errorBody = "Error message".toResponseBody("text/plain".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(404, errorBody) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertNotNull((result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle HTML error responses from Vercel`() = runTest {
        // Given
        val htmlError = "<!DOCTYPE html><html><body>502 Bad Gateway</body></html>"
        val errorBody = htmlError.toResponseBody("text/html".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(502, errorBody) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("El backend está despertando (código 502). Reintenta en unos segundos.", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle HTML error with opening html tag`() = runTest {
        // Given
        val htmlError = "<html><head><title>Error</title></head><body>Server Error</body></html>"
        val errorBody = htmlError.toResponseBody("text/html".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(503, errorBody) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("El backend está despertando (código 503). Reintenta en unos segundos.", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should return error code when error body is empty`() = runTest {
        // Given
        val errorBody = "".toResponseBody("text/plain".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(500, errorBody) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Error code: 500", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle SocketTimeoutException`() = runTest {
        // Given
        val mockCall: suspend () -> Response<String> = { throw SocketTimeoutException("Connection timeout") }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("El servidor está despertando, intenta de nuevo.", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle MalformedJsonException`() = runTest {
        // Given
        val mockCall: suspend () -> Response<String> = { 
            throw com.google.gson.stream.MalformedJsonException("Invalid JSON") 
        }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Error técnico del servidor (Posible 502/504).", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle IOException`() = runTest {
        // Given
        val errorMessage = "Network connection failed"
        val mockCall: suspend () -> Response<String> = { throw IOException(errorMessage) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle generic exceptions`() = runTest {
        // Given
        val errorMessage = "Unknown error occurred"
        val mockCall: suspend () -> Response<String> = { throw RuntimeException(errorMessage) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle exception without message`() = runTest {
        // Given
        val mockCall: suspend () -> Response<String> = { throw RuntimeException() }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Unknown Error", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should properly parse JSON error body`() = runTest {
        // Given
        val jsonError = """{"error": "Invalid product data", "code": "VALIDATION_ERROR"}"""
        val errorBody = jsonError.toResponseBody("application/json".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(400, errorBody) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertTrue((result as NetworkResult.Error).message?.contains("error") == true)
    }

    @Test
    fun `safeApiCall should handle 404 not found`() = runTest {
        // Given
        val errorBody = "Resource not found".toResponseBody("text/plain".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(404, errorBody) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Recurso no encontrado (404). Verifica ruta / api / versión.", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle 401 unauthorized`() = runTest {
        // Given
        val errorBody = "Unauthorized access".toResponseBody("text/plain".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(401, errorBody) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("No autorizado (401). Revisa credenciales o token.", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle successful response with complex object`() = runTest {
        // Given
        data class TestData(val id: String, val name: String, val value: Int)
        val expectedData = TestData("123", "Test Product", 100)
        val mockCall: suspend () -> Response<TestData> = { Response.success(expectedData) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assertEquals("123", successResult.data?.id)
        assertEquals("Test Product", successResult.data?.name)
        assertEquals(100, successResult.data?.value)
    }

    @Test
    fun `safeApiCall should handle list response`() = runTest {
        // Given
        val expectedList = listOf("Item 1", "Item 2", "Item 3")
        val mockCall: suspend () -> Response<List<String>> = { Response.success(expectedList) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(3, (result as NetworkResult.Success).data?.size)
        assertEquals("Item 1", result.data?.get(0))
    }

    @Test
    fun `safeApiCall should handle empty list response`() = runTest {
        // Given
        val emptyList = emptyList<String>()
        val mockCall: suspend () -> Response<List<String>> = { Response.success(emptyList) }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertTrue((result as NetworkResult.Success).data?.isEmpty() == true)
    }

    @Test
    fun `safeApiCall should map deployment not found error`() = runTest {
        val errorBody = "DEPLOYMENT_NOT_FOUND".toResponseBody("text/plain".toMediaTypeOrNull())
        val mockCall: suspend () -> Response<String> = { Response.error(404, errorBody) }
        val result = safeApiCall(mockCall)
        assertTrue(result is NetworkResult.Error)
        assertTrue((result as NetworkResult.Error).message?.contains("Deployment Vercel no encontrado") == true)
    }
}
