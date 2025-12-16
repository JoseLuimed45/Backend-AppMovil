package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.models.Product
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `testLogin with valid credentials returns response`() = runTest {
        // Given
        val responseJson = """
            {
                "success": true,
                "message": "Login exitoso",
                "token": "token123",
                "user": {
                    "id": 1,
                    "nombre": "Test User",
                    "correo": "test@example.com",
                    "telefono": "912345678",
                    "direccion": "Test Address"
                }
            }
        """.trimIndent()

        mockWebServer.run {
            enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(responseJson)
                    .addHeader("Content-Type", "application/json")
            )
        }

        val loginRequest = LoginRequest("test@example.com", "password123")

        // When
        val response = apiService.login(loginRequest)

        // Then
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals(200, response.code())
    }

    @Test
    fun `testLogin with invalid credentials returns 401`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("Contraseña incorrecta")
        )

        val loginRequest = LoginRequest("test@example.com", "wrongpassword")

        // When
        val response = apiService.login(loginRequest)

        // Then
        assertFalse(response.isSuccessful)
        assertEquals(401, response.code())
    }

    @Test
    fun `testGetProductos returns products list`() = runTest {
        // Given
        val responseJson = """
            [
                {
                    "id": "1",
                    "nombre": "Polera Test",
                    "descripcion": "Desc test",
                    "precio": 15000,
                    "categoria": "SERIGRAFIA",
                    "stock": 10,
                    "imagenUrl": "url"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.getProductos()

        // Then
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals(1, response.body()?.size)
    }
}

