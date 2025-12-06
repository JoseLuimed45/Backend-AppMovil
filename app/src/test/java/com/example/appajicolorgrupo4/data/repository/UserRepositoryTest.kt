package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.local.user.UserDao
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private val userDao: UserDao = mockk()

    @Before
    fun setUp() {
        userRepository = UserRepository(userDao)
    }

    @Test
    fun `login returns success when user exists locally`() = runTest {
        // Given
        val email = "test@test.com"
        val user = UserEntity(1, "Test User", email, "123456789", "Address")
        coEvery { userDao.getUserByEmail(email) } returns user

        // When
        val result = userRepository.login(email, "ignored")

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(user, (result as NetworkResult.Success).data)
    }

    @Test
    fun `login returns error when user not found locally`() = runTest {
        // Given
        val email = "nonexistent@test.com"
        coEvery { userDao.getUserByEmail(email) } returns null

        // When
        val result = userRepository.login(email, "ignored")

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Usuario no encontrado localmente", (result as NetworkResult.Error).message)
    }

    @Test
    fun `register returns success when user is created`() = runTest {
        // Given
        val nombre = "New User"
        val correo = "new@test.com"
        val telefono = "123456789"
        val clave = "password"
        val direccion = "Address"
        val userId = 1L
        
        coEvery { userDao.getUserByEmail(correo) } returns null
        coEvery { userDao.insert(any()) } returns userId

        // When
        val result = userRepository.register(nombre, correo, telefono, clave, direccion)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(userId, (result as NetworkResult.Success).data)
        coVerify { userDao.insert(any()) }
    }

    @Test
    fun `register returns error when email already exists`() = runTest {
        // Given
        val nombre = "New User"
        val correo = "existing@test.com"
        val telefono = "123456789"
        val clave = "password"
        val direccion = "Address"
        val existingUser = UserEntity(1, "Existing", correo, telefono, direccion)
        
        coEvery { userDao.getUserByEmail(correo) } returns existingUser

        // When
        val result = userRepository.register(nombre, correo, telefono, clave, direccion)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("El correo ya está registrado", (result as NetworkResult.Error).message)
    }
}
