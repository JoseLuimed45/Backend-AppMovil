package com.example.appajicolorgrupo4.viewmodel

import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.NetworkResult
import com.example.appajicolorgrupo4.data.repository.ProductRepository
import com.example.appajicolorgrupo4.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val productRepository: ProductRepository = mockk(relaxed = true)
    private lateinit var viewModel: AdminProductViewModel

    @Test
    fun `viewModel initializes successfully`() = runTest {
        val mockProducts = listOf(
            Product("1", "Polera 1", "Desc 1", 15000, "SERIGRAFIA", 10, "url1"),
            Product("2", "Polera 2", "Desc 2", 20000, "DTF", 5, "url2")
        )

        coEvery { productRepository.getProducts() } returns NetworkResult.Success(mockProducts)

        viewModel = AdminProductViewModel(productRepository)
        advanceUntilIdle()

        assertNotNull(viewModel.productos.value)
    }
}

