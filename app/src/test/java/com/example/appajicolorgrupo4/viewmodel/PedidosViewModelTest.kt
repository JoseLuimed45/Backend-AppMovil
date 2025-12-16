package com.example.appajicolorgrupo4.viewmodel

import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.data.remote.ApiService
import com.example.appajicolorgrupo4.data.repository.RemotePedidoRepository
import com.example.appajicolorgrupo4.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockMainViewModel: MainViewModel = mockk(relaxed = true)
    private val mockCarritoViewModel: CarritoViewModel = mockk(relaxed = true)
    private val mockUsuarioViewModel: UsuarioViewModel = mockk(relaxed = true)
    private val mockPedidoRepository: RemotePedidoRepository = mockk(relaxed = true)
    private lateinit var viewModel: PedidosViewModel

    @Test
    fun `viewModel initializes successfully`() = runTest {
        viewModel = PedidosViewModel(
            mainViewModel = mockMainViewModel,
            carritoViewModel = mockCarritoViewModel,
            usuarioViewModel = mockUsuarioViewModel,
            pedidoRepository = mockPedidoRepository
        )
        advanceUntilIdle()

        assertNotNull(viewModel.pedidos.value)
        assertEquals(0, viewModel.pedidos.value.size)
    }

    @Test
    fun `pedidos flow emits empty list initially`() = runTest {
        viewModel = PedidosViewModel(
            mainViewModel = mockMainViewModel,
            carritoViewModel = mockCarritoViewModel,
            usuarioViewModel = mockUsuarioViewModel,
            pedidoRepository = mockPedidoRepository
        )
        advanceUntilIdle()

        assertTrue(viewModel.pedidos.value.isEmpty())
    }
}

