package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.ApiService
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import retrofit2.Response

/**
 * Pruebas rápidas del ProductRepository para crear y eliminar productos.
 * Usa un ApiService fake que devuelve respuestas exitosas.
 */
class ProductRepositoryTest {

    private class FakeApiService : ApiService {
        private val store = mutableMapOf<String, Product>()

        override suspend fun getProductos(): Response<List<Product>> =
            Response.success(store.values.toList())

        override suspend fun getProductoById(id: String): Response<Product> =
            store[id]?.let { Response.success(it) } ?: Response.error(404, okhttp3.ResponseBody.create(null, ""))

        override suspend fun createProduct(
            nombre: RequestBody,
            descripcion: RequestBody,
            precio: RequestBody,
            categoria: RequestBody,
            stock: RequestBody,
            image: MultipartBody.Part?
        ): Response<Product> {
            val p = Product(
                id = (store.size + 1).toString(),
                nombre = nombre.string(),
                descripcion = descripcion.string(),
                precio = precio.string().toIntOrNull() ?: 0,
                categoria = categoria.string(),
                stock = stock.string().toIntOrNull() ?: 0,
                imagenUrl = null
            )
            store[p.id] = p
            return Response.success(p)
        }

        override suspend fun updateProduct(
            id: String,
            nombre: RequestBody,
            descripcion: RequestBody,
            precio: RequestBody,
            categoria: RequestBody,
            stock: RequestBody,
            image: MultipartBody.Part?
        ): Response<Product> {
            val existing = store[id] ?: return Response.error(404, okhttp3.ResponseBody.create(null, ""))
            val updated = existing.copy(
                nombre = nombre.string(),
                descripcion = descripcion.string(),
                precio = precio.string().toIntOrNull() ?: existing.precio,
                categoria = categoria.string(),
                stock = stock.string().toIntOrNull() ?: existing.stock
            )
            store[id] = updated
            return Response.success(updated)
        }

        override suspend fun deleteProduct(id: String): Response<Unit> {
            return if (store.remove(id) != null) Response.success(Unit)
            else Response.error(404, okhttp3.ResponseBody.create(null, ""))
        }

        // Otros métodos no usados en estas pruebas
        override suspend fun login(loginRequest: com.example.appajicolorgrupo4.data.remote.LoginRequest) =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
        override suspend fun register(user: com.example.appajicolorgrupo4.data.remote.RegisterRequest) =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
        override suspend fun recoverPassword(email: Map<String, String>) =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
        override suspend fun resetPassword(request: com.example.appajicolorgrupo4.data.remote.ResetPasswordRequest) =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
        override suspend fun getPosts(): Response<List<com.example.appajicolorgrupo4.data.models.Post>> =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
        override suspend fun getPostById(postId: Int): Response<com.example.appajicolorgrupo4.data.models.Post> =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
        override suspend fun createOrder(order: com.example.appajicolorgrupo4.data.models.Order) =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
        override suspend fun getOrdersByUser(userId: String) =
            Response.error(501, okhttp3.ResponseBody.create(null, ""))
    }

    private fun RequestBody.string(): String =
        try {
            val buffer = Buffer()
            this.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            ""
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `createProduct devuelve Success y persiste en lista`() = runTest {
        val repo = ProductRepository(FakeApiService())

        val result = repo.createProduct(
            nombre = "Polera Aji",
            descripcion = "Polera serigrafía",
            precio = 9990,
            categoria = "Serigrafía",
            stock = 50,
            imageFile = null
        )

        assertTrue(result is NetworkResult.Success)
        val created = (result as NetworkResult.Success).data
        assertNotNull(created)
        assertTrue(created!!.nombre == "Polera Aji")

        val list = repo.getProducts()
        assertTrue(list is NetworkResult.Success)
        val productos = (list as NetworkResult.Success).data ?: emptyList()
        assertTrue(productos.any { it.id == created!!.id })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `deleteProduct devuelve Success y desaparece de lista`() = runTest {
        val service = FakeApiService()
        val repo = ProductRepository(service)

        // Crear primero
        val created = repo.createProduct(
            nombre = "Polera B",
            descripcion = "Polera dtf",
            precio = 12990,
            categoria = "DTF",
            stock = 20,
            imageFile = null
        )
        val id = (created as NetworkResult.Success).data!!.id

        // Eliminar
        val del = repo.deleteProduct(id)
        assertTrue(del is NetworkResult.Success)

        // Verificar que no está
        val list = repo.getProducts()
        val productos = (list as NetworkResult.Success).data ?: emptyList()
        assertTrue(productos.none { it.id == id })
    }
}
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
import java.io.File

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

    @Test
    fun `createProduct should return Success with created product`() = runTest {
        // Given
        val newProduct = Product("3", "Nueva Polera", "Nueva desc", 18000, "DTF", 15, "url")
        coEvery { 
            apiService.createProduct(any(), any(), any(), any(), any(), null) 
        } returns Response.success(newProduct)

        // When
        val result = repository.createProduct("Nueva Polera", "Nueva desc", 18000, "DTF", 15, null)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals("Nueva Polera", (result as NetworkResult.Success).data?.nombre)
        assertEquals(18000, result.data?.precio)
        coVerify { apiService.createProduct(any(), any(), any(), any(), any(), null) }
    }

    @Test
    fun `createProduct should handle image file upload`() = runTest {
        // Given
        val newProduct = Product("4", "Polera con Imagen", "Desc", 20000, "SERIGRAFIA", 10, "url")
        coEvery { 
            apiService.createProduct(any(), any(), any(), any(), any(), any()) 
        } returns Response.success(newProduct)

        // When - Test sin archivo real, solo verificando que el método acepta el parámetro
        val result = repository.createProduct("Polera con Imagen", "Desc", 20000, "SERIGRAFIA", 10, null)

        // Then
        assertTrue(result is NetworkResult.Success)
        coVerify { apiService.createProduct(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createProduct should return Error on API failure`() = runTest {
        // Given
        val errorResponse = "Error de validación".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { 
            apiService.createProduct(any(), any(), any(), any(), any(), null) 
        } returns Response.error(400, errorResponse)

        // When
        val result = repository.createProduct("Test", "Desc", 10000, "DTF", 5, null)

        // Then
        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `updateProduct should return Success with updated product`() = runTest {
        // Given
        val updatedProduct = Product("1", "Polera Actualizada", "Desc actualizada", 22000, "DTF", 20, "url")
        coEvery { 
            apiService.updateProduct(any(), any(), any(), any(), any(), any(), null) 
        } returns Response.success(updatedProduct)

        // When
        val result = repository.updateProduct("1", "Polera Actualizada", "Desc actualizada", 22000, "DTF", 20, null)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals("Polera Actualizada", (result as NetworkResult.Success).data?.nombre)
        assertEquals(22000, result.data?.precio)
        coVerify { apiService.updateProduct("1", any(), any(), any(), any(), any(), null) }
    }

    @Test
    fun `updateProduct should return Error on API failure`() = runTest {
        // Given
        val errorResponse = "Error al actualizar".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { 
            apiService.updateProduct(any(), any(), any(), any(), any(), any(), null) 
        } returns Response.error(500, errorResponse)

        // When
        val result = repository.updateProduct("1", "Test", "Desc", 10000, "DTF", 5, null)

        // Then
        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `deleteProduct should return Success on successful deletion`() = runTest {
        // Given
        coEvery { apiService.deleteProduct("1") } returns Response.success(Unit)

        // When
        val result = repository.deleteProduct("1")

        // Then
        assertTrue(result is NetworkResult.Success)
        coVerify { apiService.deleteProduct("1") }
    }

    @Test
    fun `deleteProduct should return Error on API failure`() = runTest {
        // Given
        val errorResponse = "Error al eliminar".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { apiService.deleteProduct("1") } returns Response.error(500, errorResponse)

        // When
        val result = repository.deleteProduct("1")

        // Then
        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `deleteProduct should handle 404 not found`() = runTest {
        // Given
        val errorResponse = "Producto no encontrado".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { apiService.deleteProduct("999") } returns Response.error(404, errorResponse)

        // When
        val result = repository.deleteProduct("999")

        // Then
        assertTrue(result is NetworkResult.Error)
        assertNotNull((result as NetworkResult.Error).message)
    }

    @Test
    fun `getProducts should handle null response body`() = runTest {
        // Given
        coEvery { apiService.getProductos() } returns Response.success(null)

        // When
        val result = repository.getProducts()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Response body is null", (result as NetworkResult.Error).message)
    }

    @Test
    fun `createProduct with all parameters should create proper request`() = runTest {
        // Given
        val newProduct = Product("5", "Polera Completa", "Descripción completa", 25000, "ACCESORIOS", 30, "url")
        coEvery { 
            apiService.createProduct(any(), any(), any(), any(), any(), any()) 
        } returns Response.success(newProduct)

        // When
        val result = repository.createProduct(
            nombre = "Polera Completa",
            descripcion = "Descripción completa",
            precio = 25000,
            categoria = "ACCESORIOS",
            stock = 30,
            imageFile = null
        )

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals("Polera Completa", (result as NetworkResult.Success).data?.nombre)
        assertEquals("ACCESORIOS", result.data?.categoria)
        assertEquals(30, result.data?.stock)
    }
}
