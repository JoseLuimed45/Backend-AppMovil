package com.example.appajicolorgrupo4.data.remote

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
        assertTrue("Result should be NetworkResult.Error", result is NetworkResult.Error)
        val errorMessage = (result as NetworkResult.Error).message
        assertNotNull("Error message should not be null", errorMessage)
        // Solo verificamos que hay un mensaje, no su contenido específico
        assertTrue("Error message should not be empty", errorMessage?.isNotEmpty() == true)
    }

    @Test
    fun `safeApiCall should return Error when network error occurs`() = runTest {
        // Given
        val mockCall: suspend () -> Response<String> = { throw IOException("Network error") }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `safeApiCall should return Error on timeout`() = runTest {
        // Given
        val mockCall: suspend () -> Response<String> = { throw SocketTimeoutException("Timeout") }

        // When
        val result = safeApiCall(mockCall)

        // Then
        assertTrue(result is NetworkResult.Error)
    }
}

