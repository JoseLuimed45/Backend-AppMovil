package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.ApiService
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ProductRepositoryTest {

    private lateinit var repository: ProductRepository
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        apiService = mockk()
        repository = ProductRepository(apiService)
    }

    @Test
    fun `getProducts should return Success with products list`() = runTest {
        // Given
        val mockProducts = listOf(
            Product("1", "Polera 1", "Desc 1", 15000, "SERIGRAFIA", 10, "url1"),
            Product("2", "Polera 2", "Desc 2", 20000, "DTF", 5, "url2")
        )
        coEvery { apiService.getProductos() } returns Response.success(mockProducts)

        // When
        val result = repository.getProducts()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, (result as NetworkResult.Success).data?.size)
        assertEquals("Polera 1", result.data?.get(0)?.nombre)
        coVerify(exactly = 1) { apiService.getProductos() }
    }

    @Test
    fun `getProducts should return Error on API failure`() = runTest {
        // Given
        val errorResponse = "Error del servidor".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { apiService.getProductos() } returns Response.error(500, errorResponse)

        // When
        val result = repository.getProducts()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertNotNull((result as NetworkResult.Error).message)
    }

    @Test
    fun `getProductById should return Success with single product`() = runTest {
        // Given
        val mockProduct = Product("1", "Polera Test", "Descripción", 15000, "SERIGRAFIA", 10, "url")
        coEvery { apiService.getProductoById("1") } returns Response.success(mockProduct)

        // When
        val result = repository.getProductById("1")

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals("Polera Test", (result as NetworkResult.Success).data?.nombre)
        coVerify { apiService.getProductoById("1") }
    }

    @Test
    fun `getProductById should return Error when product not found`() = runTest {
        // Given
        val errorResponse = "Producto no encontrado".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { apiService.getProductoById("999") } returns Response.error(404, errorResponse)

        // When
        val result = repository.getProductById("999")

        // Then
        assertTrue(result is NetworkResult.Error)
    }
}

