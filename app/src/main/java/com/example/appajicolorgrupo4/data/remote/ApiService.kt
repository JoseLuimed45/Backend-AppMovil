package com.example.appajicolorgrupo4.data.remote

import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.models.LoginResponse
import com.example.appajicolorgrupo4.data.models.Post
import com.example.appajicolorgrupo4.data.models.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Interfaz que define las operaciones de la API utilizando Retrofit. Cada método en esta interfaz
 * corresponde a un endpoint de la API. Retrofit se encargará de generar la implementación de esta
 * interfaz para realizar las llamadas de red reales.
 */
interface ApiService {

        // ==================== AUTENTICACIÓN ====================

        /** Login de usuario POST /api/v1/usuarios/login */
        @POST("api/v1/usuarios/login")
        suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

        /** Registro de nuevo usuario POST /api/v1/usuarios/register */
        @POST("api/v1/usuarios/register")
        suspend fun register(@Body user: RegisterRequest): Response<UserEntity>

        /** Solicitar recuperación de contraseña POST /api/v1/usuarios/recover */
        @POST("api/v1/usuarios/recover")
        suspend fun recoverPassword(@Body email: Map<String, String>): Response<RecoverResponse>

        /** Restablecer contraseña POST /api/v1/usuarios/reset-password */
        @POST("api/v1/usuarios/reset-password")
        suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetResponse>

        /** Actualizar perfil de usuario PUT /api/v1/usuarios/:id */
        @retrofit2.http.PUT("api/v1/usuarios/{id}")
        suspend fun updatePerfil(
                @Path("id") id: String,
                @Body perfil: UpdatePerfilRequest
        ): Response<UpdatePerfilResponse>

        // ==================== PRODUCTOS ====================

        /** Obtiene lista de productos GET /api/v1/productos */
        @GET("api/v1/productos") suspend fun getProductos(): Response<List<Product>>

        /** Obtiene un producto por ID GET /api/v1/productos/{id} */
        @GET("api/v1/productos/{id}")
        suspend fun getProductoById(@Path("id") id: String): Response<Product>

        /** Crea un nuevo producto con JSON (Admin - sin imagen) POST /api/v1/productos */
        @POST("api/v1/productos")
        suspend fun createProductJson(@Body product: CreateProductRequest): Response<Product>

        /** Crea un nuevo producto con imagen (Admin) POST /api/v1/productos */
        @Multipart
        @POST("api/v1/productos")
        suspend fun createProduct(
                @Part("id") id: RequestBody,
                @Part("nombre") nombre: RequestBody,
                @Part("descripcion") descripcion: RequestBody,
                @Part("precio") precio: RequestBody,
                @Part("categoria") categoria: RequestBody,
                @Part("stock") stock: RequestBody,
                @Part image: MultipartBody.Part?
        ): Response<Product>

        /**
         * Actualiza un producto existente con JSON (Admin - sin imagen) PUT /api/v1/productos/{id}
         */
        @retrofit2.http.PUT("api/v1/productos/{id}")
        suspend fun updateProductJson(
                @Path("id") id: String,
                @Body product: UpdateProductRequest
        ): Response<Product>

        /** Actualiza un producto existente (Admin) PUT /api/v1/productos/{id} */
        @Multipart
        @retrofit2.http.PUT("api/v1/productos/{id}")
        suspend fun updateProduct(
                @Path("id") id: String,
                @Part("nombre") nombre: RequestBody,
                @Part("descripcion") descripcion: RequestBody,
                @Part("precio") precio: RequestBody,
                @Part("categoria") categoria: RequestBody,
                @Part("stock") stock: RequestBody,
                @Part image: MultipartBody.Part?
        ): Response<Product>

        /** Elimina un producto (Admin) DELETE /api/v1/productos/{id} */
        @retrofit2.http.DELETE("api/v1/productos/{id}")
        suspend fun deleteProduct(@Path("id") id: String): Response<Unit>

        // ==================== ADMIN PANEL ====================

        /** Listar todos los usuarios (Admin) GET /api/v1/admin/usuarios */
        @GET("api/v1/admin/usuarios") suspend fun listarUsuarios(): Response<List<UserEntity>>

        /** Listar todos los pedidos (Admin) GET /api/v1/admin/pedidos */
        @GET("api/v1/admin/pedidos") suspend fun listarPedidos(): Response<List<Map<String, Any>>>

        /** Obtener estadísticas (Admin) GET /api/v1/admin/estadisticas */
        @GET("api/v1/admin/estadisticas")
        suspend fun obtenerEstadisticas(): Response<Map<String, Any>>

        // ==================== POSTS (Para admin) ====================

        /**
         * Obtiene la lista completa de todas las publicaciones (posts) desde la API. La anotación
         * @GET especifica el tipo de petición HTTP y el endpoint relativo.
         * @return Un objeto Response que contiene una lista de objetos Post. Usar Response<T> nos
         * permite verificar el código de estado HTTP y manejar errores
         */
        @GET("api/v1/posts") // Asumiendo que posts es parte de algún microservicio
        suspend fun getPosts(): Response<List<Post>>

        /**
         * Obtiene una única publicación por su ID. El valor {id} en la URL será reemplazado
         * dinámicamente por el parámetro del método anotado con @Path.
         *
         * @param postId El ID de la publicación que se desea obtener.
         * @return Un objeto Response que contiene un único objeto Post.
         */
        @GET("api/v1/posts/{id}") suspend fun getPostById(@Path("id") postId: Int): Response<Post>

        // ==================== PEDIDOS ====================

        /** Crea un nuevo pedido POST /api/v1/pedidos */
        @POST("api/v1/pedidos")
        suspend fun createOrder(
                @Body order: com.example.appajicolorgrupo4.data.models.Order
        ): Response<com.example.appajicolorgrupo4.data.models.Order>

        /** Obtiene los pedidos de un usuario GET /api/v1/pedidos/usuario/{userId} */
        @GET("api/v1/pedidos/usuario/{userId}")
        suspend fun getOrdersByUser(
                @Path("userId") userId: String
        ): Response<List<com.example.appajicolorgrupo4.data.models.Order>>
}

// DTOs para login/register
data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
        val nombre: String,
        val email: String,
        val password: String,
        val telefono: String?,
        val direccion: String?,
        val rol: String? = "USER"
)

data class UpdatePerfilRequest(val nombre: String, val telefono: String, val direccion: String)

data class RecoverResponse(
        val message: String,
        val recoveryCode: String? // Only for test env
)

data class ResetPasswordRequest(
        val email: String,
        val recoveryCode: String,
        val newPassword: String
)

data class ResetResponse(val message: String, val token: String)

data class CreateProductRequest(
        val id: String, // ID único para el producto
        val nombre: String,
        val descripcion: String,
        val precio: Int,
        val categoria: String,
        val stock: Int
)

data class UpdateProductRequest(
        val nombre: String,
        val descripcion: String,
        val precio: Int,
        val categoria: String,
        val stock: Int
)
