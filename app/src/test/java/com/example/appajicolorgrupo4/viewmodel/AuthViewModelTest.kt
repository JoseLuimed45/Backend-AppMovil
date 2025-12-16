package com.example.appajicolorgrupo4.viewmodel

import app.cash.turbine.test
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.remote.LoginData
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.navigation.Screen
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        mainViewModel = mockk(relaxed = true)
        authViewModel = AuthViewModel(userRepository, sessionManager, mainViewModel)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dado un login exitoso para un usuario normal, se guarda sesion y se navega a Home`() = runTest {
        val email = "user@test.com"
        val password = "password123"
        val mockUser = UserEntity(
            id = 1L,
            mongoId = "123abc",
            nombre = "Test User",
            correo = email,
            telefono = "999999999",
            direccion = "Calle Test 123"
        )
        val mockLoginData = LoginData(user = mockUser, token = "fake-token-123", rol = "USER")
        authViewModel.onLoginEmailChange(email)
        authViewModel.onLoginPassChange(password)
        coEvery { userRepository.login(email, password) } returns NetworkResult.Success(mockLoginData)

        authViewModel.submitLogin()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { sessionManager.saveSession(mockUser) }
        coVerify { sessionManager.saveToken("fake-token-123") }
        coVerify { sessionManager.saveIsAdmin(false) }
        coVerify { mainViewModel.navigate(route = Screen.Home.route, popUpToRoute = Screen.Login.route, inclusive = true) }
    }

    @Test
    fun `dado un login exitoso para admin, se guarda sesion y se navega a AdminProductos`() = runTest {
        val email = "admin@test.com"
        val password = "adminpass"
        val mockAdminUser = UserEntity(
            id = 2L,
            mongoId = "456def",
            nombre = "Admin User",
            correo = email,
            telefono = "888888888",
            direccion = "Admin Street 456"
        )
        val mockLoginData = LoginData(user = mockAdminUser, token = "fake-admin-token-456", rol = "ADMIN")
        authViewModel.onLoginEmailChange(email)
        authViewModel.onLoginPassChange(password)
        coEvery { userRepository.login(email, password) } returns NetworkResult.Success(mockLoginData)

        authViewModel.submitLogin()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { sessionManager.saveSession(mockAdminUser) }
        coVerify { sessionManager.saveToken("fake-admin-token-456") }
        coVerify { sessionManager.saveIsAdmin(true) }
        coVerify { mainViewModel.navigate(route = Screen.AdminProductos.route, popUpToRoute = Screen.Login.route, inclusive = true) }
    }

    @Test
    fun `dado un login fallido, cuando se llama a submitLogin, entonces se actualiza el estado con un mensaje de error`() = runTest {
        // Arrange
        val email = "user@test.com"
        val password = "wrongpassword"
        val errorMessage = "Credenciales inválidas"
        
        authViewModel.onLoginEmailChange(email)
        authViewModel.onLoginPassChange(password)
        
        coEvery { userRepository.login(email, password) } returns NetworkResult.Error(errorMessage)

        // Act
        authViewModel.submitLogin()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val finalState = authViewModel.login.value
        assertNotNull(finalState.errorMsg)
        assertTrue(finalState.errorMsg?.contains("inválidas") == true || finalState.errorMsg == errorMessage)

        // Verificamos que NO se intentó guardar la sesión ni navegar
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
        coVerify(exactly = 0) { mainViewModel.navigate(route = any<String>(), popUpToRoute = any(), inclusive = any(), singleTop = any()) }
    }
}
