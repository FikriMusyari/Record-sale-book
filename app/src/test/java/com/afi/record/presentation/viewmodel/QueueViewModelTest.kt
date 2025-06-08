package com.afi.record.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afi.record.domain.models.*
import com.afi.record.domain.repository.QueueRepo
import com.afi.record.domain.useCase.AuthResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class QueueViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockQueueRepo = mockk<QueueRepo>()
    private lateinit var viewModel: QueueViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = QueueViewModel(mockQueueRepo)
    }

    @Test
    fun `selectCustomer should update selectedCustomer state`() = runTest {
        // Given
        val customer = Customers(1, "Test Customer", BigDecimal("1000"), 1)

        // When
        viewModel.selectCustomer(customer)

        // Then
        val selectedCustomer = viewModel.selectedCustomer.first()
        assertEquals(customer, selectedCustomer)
    }

    @Test
    fun `clearSelectedCustomer should set selectedCustomer to null`() = runTest {
        // Given
        val customer = Customers(1, "Test Customer", BigDecimal("1000"), 1)
        viewModel.selectCustomer(customer)

        // When
        viewModel.clearSelectedCustomer()

        // Then
        val selectedCustomer = viewModel.selectedCustomer.first()
        assertNull(selectedCustomer)
    }

    @Test
    fun `addSelectedProduct should calculate total price correctly`() = runTest {
        // Given
        val product = Products(1, "Test Product", BigDecimal("100.00"), 1)
        val quantity = 3
        val discount = BigDecimal("10.00")
        val expectedTotal = BigDecimal("100.00").multiply(BigDecimal(3)).subtract(BigDecimal("10.00")) // 290.00

        // When
        viewModel.addSelectedProduct(product, quantity, discount)

        // Then
        val selectedProducts = viewModel.selectedProducts.first()
        assertEquals(1, selectedProducts.size)
        assertEquals(product, selectedProducts[0].product)
        assertEquals(quantity, selectedProducts[0].quantity)
        assertEquals(discount, selectedProducts[0].discount)
        assertEquals(expectedTotal, selectedProducts[0].totalPrice)
    }

    @Test
    fun `addSelectedProduct should update existing product if same product added`() = runTest {
        // Given
        val product = Products(1, "Test Product", BigDecimal("100.00"), 1)
        viewModel.addSelectedProduct(product, 2, BigDecimal("5.00"))

        // When - add same product with different quantity and discount
        viewModel.addSelectedProduct(product, 3, BigDecimal("10.00"))

        // Then
        val selectedProducts = viewModel.selectedProducts.first()
        assertEquals(1, selectedProducts.size) // Should still be 1 product
        assertEquals(3, selectedProducts[0].quantity) // Updated quantity
        assertEquals(BigDecimal("10.00"), selectedProducts[0].discount) // Updated discount
    }

    @Test
    fun `removeSelectedProduct should remove product from list`() = runTest {
        // Given
        val product1 = Products(1, "Product 1", BigDecimal("100.00"), 1)
        val product2 = Products(2, "Product 2", BigDecimal("200.00"), 1)
        viewModel.addSelectedProduct(product1, 1, BigDecimal.ZERO)
        viewModel.addSelectedProduct(product2, 1, BigDecimal.ZERO)

        // When
        viewModel.removeSelectedProduct(1)

        // Then
        val selectedProducts = viewModel.selectedProducts.first()
        assertEquals(1, selectedProducts.size)
        assertEquals(product2, selectedProducts[0].product)
    }

    @Test
    fun `clearAllSelections should clear both customer and products`() = runTest {
        // Given
        val customer = Customers(1, "Test Customer", BigDecimal("1000"), 1)
        val product = Products(1, "Test Product", BigDecimal("100.00"), 1)
        viewModel.selectCustomer(customer)
        viewModel.addSelectedProduct(product, 1, BigDecimal.ZERO)

        // When
        viewModel.clearAllSelections()

        // Then
        val selectedCustomer = viewModel.selectedCustomer.first()
        val selectedProducts = viewModel.selectedProducts.first()
        assertNull(selectedCustomer)
        assertTrue(selectedProducts.isEmpty())
    }

    @Test
    fun `createQueue should call repository and update state on success`() = runTest {
        // Given
        val request = CreateQueueRequest(
            customerId = 1,
            statusId = 1,
            paymentId = null,
            note = "Test note",
            orders = listOf(OrderItem(1, 2, BigDecimal("5.00")))
        )
        val mockResponse = QueueResponse(data = emptyList())
        coEvery { mockQueueRepo.createQueue(request) } returns mockResponse
        coEvery { mockQueueRepo.getAllQueue() } returns mockResponse

        // When
        viewModel.createQueue(request)
        advanceUntilIdle()

        // Then
        val result = viewModel.queue.first()
        assertTrue(result is AuthResult.Success<*>)
        assertEquals("🎉 Antrian berhasil dibuat!", (result as AuthResult.Success<*>).message)
        coVerify { mockQueueRepo.createQueue(request) }
        coVerify { mockQueueRepo.getAllQueue() }
    }

    @Test
    fun `createQueue should handle error and show error message`() = runTest {
        // Given
        val request = CreateQueueRequest(
            customerId = 1,
            statusId = 1,
            paymentId = null,
            note = "Test note",
            orders = listOf(OrderItem(1, 2, BigDecimal("5.00")))
        )
        val exception = RuntimeException("400 Bad Request")
        coEvery { mockQueueRepo.createQueue(request) } throws exception

        // When
        viewModel.createQueue(request)
        advanceUntilIdle()

        // Then
        val result = viewModel.queue.first()
        assertTrue(result is AuthResult.Error)
        assertTrue((result as AuthResult.Error).message.contains("Data antrian tidak valid"))
        coVerify { mockQueueRepo.createQueue(request) }
    }

    @Test
    fun `getAllQueues should load queues successfully`() = runTest {
        // Given
        val mockQueues = listOf(
            DataItem(id = 1, customer = "Customer 1", status = "In queue"),
            DataItem(id = 2, customer = "Customer 2", status = "Completed")
        )
        val mockResponse = QueueResponse(data = mockQueues)
        coEvery { mockQueueRepo.getAllQueue() } returns mockResponse

        // When
        viewModel.getAllQueues()
        advanceUntilIdle()

        // Then
        val result = viewModel.queue.first()
        val queues = viewModel.queues.first()
        assertTrue(result is AuthResult.Success<*>)
        assertEquals(mockQueues, queues)
        coVerify { mockQueueRepo.getAllQueue() }
    }

    @Test
    fun `updateQueue should call repository and refresh queue list`() = runTest {
        // Given
        val queueId = 1
        val request = UpdateQueueRequest(statusId = 2)
        val mockResponse = QueueResponse(data = emptyList())
        coEvery { mockQueueRepo.updateQueue(queueId, request) } returns Unit
        coEvery { mockQueueRepo.getAllQueue() } returns mockResponse

        // When
        viewModel.updateQueue(queueId, request)
        advanceUntilIdle()

        // Then
        val result = viewModel.queue.first()
        assertTrue(result is AuthResult.Success<*>)
        assertEquals("🎉 Antrian berhasil diperbarui!", (result as AuthResult.Success<*>).message)
        coVerify { mockQueueRepo.updateQueue(queueId, request) }
        coVerify { mockQueueRepo.getAllQueue() }
    }

    @Test
    fun `deleteQueue should call repository and refresh queue list`() = runTest {
        // Given
        val queueId = 1
        val mockResponse = QueueResponse(data = emptyList())
        coEvery { mockQueueRepo.deleteQueue(queueId) } returns Unit
        coEvery { mockQueueRepo.getAllQueue() } returns mockResponse

        // When
        viewModel.deleteQueue(queueId)
        advanceUntilIdle()

        // Then
        val result = viewModel.queue.first()
        assertTrue(result is AuthResult.Success<*>)
        assertEquals("🗑️ Antrian berhasil dihapus!", (result as AuthResult.Success<*>).message)
        coVerify { mockQueueRepo.deleteQueue(queueId) }
        coVerify { mockQueueRepo.getAllQueue() }
    }

    @Test
    fun `setTempSelectedProduct should update tempSelectedProduct state`() = runTest {
        // Given
        val product = Products(1, "Test Product", BigDecimal("100.00"), 1)

        // When
        viewModel.setTempSelectedProduct(product)

        // Then
        val tempProduct = viewModel.tempSelectedProduct.first()
        assertEquals(product, tempProduct)
    }

    @Test
    fun `clearTempSelectedProduct should set tempSelectedProduct to null`() = runTest {
        // Given
        val product = Products(1, "Test Product", BigDecimal("100.00"), 1)
        viewModel.setTempSelectedProduct(product)

        // When
        viewModel.clearTempSelectedProduct()

        // Then
        val tempProduct = viewModel.tempSelectedProduct.first()
        assertNull(tempProduct)
    }

    @Test
    fun `clearQueueError should reset error state to idle`() = runTest {
        // Given - simulate an error state
        val request = CreateQueueRequest(1, 1, null, null, emptyList())
        coEvery { mockQueueRepo.createQueue(request) } throws RuntimeException("Test error")
        viewModel.createQueue(request)
        advanceUntilIdle()

        // When
        viewModel.clearQueueError()

        // Then
        val result = viewModel.queue.first()
        assertTrue(result is AuthResult.Idle)
    }
}
