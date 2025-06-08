package com.afi.record.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afi.record.data.repositoryImpl.QueueRepoImpl
import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.*
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.presentation.viewmodel.QueueViewModel
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
class QueueManagementFlowTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockApiService = mockk<ApiService>()
    private lateinit var queueRepo: QueueRepoImpl
    private lateinit var queueViewModel: QueueViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        queueRepo = QueueRepoImpl(mockApiService)
        queueViewModel = QueueViewModel(queueRepo)
    }

    @Test
    fun `complete queue creation flow should work end to end`() = runTest {
        // Given - Setup mock data
        val customer = Customers(1, "John Doe", BigDecimal("5000"), 1)
        val product1 = Products(1, "Product A", BigDecimal("100.50"), 1)
        val product2 = Products(2, "Product B", BigDecimal("250.75"), 1)
        
        val orders = listOf(
            OrderItem(productId = 1, quantity = 2, discount = BigDecimal("10.00")),
            OrderItem(productId = 2, quantity = 1, discount = BigDecimal("5.00"))
        )
        
        val createRequest = CreateQueueRequest(
            customerId = 1,
            statusId = 1,
            paymentId = null,
            note = "Test order",
            orders = orders
        )
        
        val mockQueueResponse = QueueResponse(
            data = listOf(
                DataItem(
                    id = 1,
                    customer = "John Doe",
                    status = "In queue",
                    grandTotal = 436,
                    note = "Test order"
                )
            )
        )
        
        coEvery { mockApiService.createQueue(createRequest) } returns mockQueueResponse
        coEvery { mockApiService.getAllQueue() } returns mockQueueResponse

        // When - Execute complete flow
        // 1. Select customer
        queueViewModel.selectCustomer(customer)
        
        // 2. Add products
        queueViewModel.addSelectedProduct(product1, 2, BigDecimal("10.00"))
        queueViewModel.addSelectedProduct(product2, 1, BigDecimal("5.00"))
        
        // 3. Create queue
        queueViewModel.createQueue(createRequest)
        advanceUntilIdle()

        // Then - Verify results
        val selectedCustomer = queueViewModel.selectedCustomer.first()
        val selectedProducts = queueViewModel.selectedProducts.first()
        val queueResult = queueViewModel.queue.first()
        val queues = queueViewModel.queues.first()

        // Verify customer selection
        assertEquals(customer, selectedCustomer)
        
        // Verify product selections and calculations
        assertEquals(2, selectedProducts.size)
        
        val selectedProduct1 = selectedProducts.find { it.product.id == 1 }
        assertNotNull(selectedProduct1)
        assertEquals(2, selectedProduct1!!.quantity)
        assertEquals(BigDecimal("10.00"), selectedProduct1.discount)
        assertEquals(BigDecimal("191.00"), selectedProduct1.totalPrice) // (100.50 * 2) - 10.00
        
        val selectedProduct2 = selectedProducts.find { it.product.id == 2 }
        assertNotNull(selectedProduct2)
        assertEquals(1, selectedProduct2!!.quantity)
        assertEquals(BigDecimal("5.00"), selectedProduct2.discount)
        assertEquals(BigDecimal("245.75"), selectedProduct2.totalPrice) // (250.75 * 1) - 5.00
        
        // Verify queue creation
        assertTrue(queueResult is AuthResult.Success<*>)
        assertEquals("🎉 Antrian berhasil dibuat!", (queueResult as AuthResult.Success<*>).message)
        
        // Verify queue list updated
        assertEquals(1, queues.size)
        assertEquals("John Doe", queues[0].customer)
        assertEquals("In queue", queues[0].status)
        
        // Verify API calls
        coVerify { mockApiService.createQueue(createRequest) }
        coVerify { mockApiService.getAllQueue() }
    }

    @Test
    fun `queue update flow should work correctly`() = runTest {
        // Given
        val queueId = 1
        val updateRequest = UpdateQueueRequest(statusId = 4) // Change to completed
        val updatedQueueResponse = QueueResponse(
            data = listOf(
                DataItem(
                    id = 1,
                    customer = "John Doe",
                    status = "Completed",
                    grandTotal = 436
                )
            )
        )
        
        coEvery { mockApiService.updateQueue(queueId, updateRequest) } returns Unit
        coEvery { mockApiService.getAllQueue() } returns updatedQueueResponse

        // When
        queueViewModel.updateQueue(queueId, updateRequest)
        advanceUntilIdle()

        // Then
        val queueResult = queueViewModel.queue.first()
        val queues = queueViewModel.queues.first()
        
        assertTrue(queueResult is AuthResult.Success<*>)
        assertEquals("🎉 Antrian berhasil diperbarui!", (queueResult as AuthResult.Success<*>).message)
        assertEquals("Completed", queues[0].status)
        
        coVerify { mockApiService.updateQueue(queueId, updateRequest) }
        coVerify { mockApiService.getAllQueue() }
    }

    @Test
    fun `queue deletion flow should work correctly`() = runTest {
        // Given
        val queueId = 1
        val emptyQueueResponse = QueueResponse(data = emptyList())
        
        coEvery { mockApiService.deleteQueue(queueId) } returns Unit
        coEvery { mockApiService.getAllQueue() } returns emptyQueueResponse

        // When
        queueViewModel.deleteQueue(queueId)
        advanceUntilIdle()

        // Then
        val queueResult = queueViewModel.queue.first()
        val queues = queueViewModel.queues.first()
        
        assertTrue(queueResult is AuthResult.Success<*>)
        assertEquals("🗑️ Antrian berhasil dihapus!", (queueResult as AuthResult.Success<*>).message)
        assertTrue(queues.isEmpty())
        
        coVerify { mockApiService.deleteQueue(queueId) }
        coVerify { mockApiService.getAllQueue() }
    }

    @Test
    fun `product selection and calculation should handle edge cases`() = runTest {
        // Given
        val product = Products(1, "Expensive Product", BigDecimal("999.99"), 1)
        
        // When - Add product with large quantity and discount
        queueViewModel.addSelectedProduct(product, 10, BigDecimal("100.00"))
        
        // Then
        val selectedProducts = queueViewModel.selectedProducts.first()
        assertEquals(1, selectedProducts.size)
        
        val selectedProduct = selectedProducts[0]
        assertEquals(10, selectedProduct.quantity)
        assertEquals(BigDecimal("100.00"), selectedProduct.discount)
        // Total: (999.99 * 10) - 100.00 = 9899.90
        assertEquals(BigDecimal("9899.90"), selectedProduct.totalPrice)
    }

    @Test
    fun `product selection should handle zero discount correctly`() = runTest {
        // Given
        val product = Products(1, "Test Product", BigDecimal("50.25"), 1)
        
        // When - Add product with zero discount
        queueViewModel.addSelectedProduct(product, 3, BigDecimal.ZERO)
        
        // Then
        val selectedProducts = queueViewModel.selectedProducts.first()
        val selectedProduct = selectedProducts[0]
        
        assertEquals(BigDecimal.ZERO, selectedProduct.discount)
        assertEquals(BigDecimal("150.75"), selectedProduct.totalPrice) // 50.25 * 3
    }

    @Test
    fun `error handling should work for network failures`() = runTest {
        // Given
        val createRequest = CreateQueueRequest(
            customerId = 1,
            statusId = 1,
            paymentId = null,
            note = "Test",
            orders = listOf(OrderItem(1, 1, BigDecimal.ZERO))
        )
        val networkException = RuntimeException("network timeout")
        
        coEvery { mockApiService.createQueue(createRequest) } throws networkException

        // When
        queueViewModel.createQueue(createRequest)
        advanceUntilIdle()

        // Then
        val queueResult = queueViewModel.queue.first()
        assertTrue(queueResult is AuthResult.Error)
        assertTrue((queueResult as AuthResult.Error).message.contains("Gagal membuat antrian"))
    }

    @Test
    fun `temp product selection should work correctly`() = runTest {
        // Given
        val product = Products(1, "Temp Product", BigDecimal("75.00"), 1)
        
        // When
        queueViewModel.setTempSelectedProduct(product)
        
        // Then
        val tempProduct = queueViewModel.tempSelectedProduct.first()
        assertEquals(product, tempProduct)
        
        // When - Clear temp selection
        queueViewModel.clearTempSelectedProduct()
        
        // Then
        val clearedTempProduct = queueViewModel.tempSelectedProduct.first()
        assertNull(clearedTempProduct)
    }

    @Test
    fun `multiple product additions should accumulate correctly`() = runTest {
        // Given
        val product1 = Products(1, "Product 1", BigDecimal("10.00"), 1)
        val product2 = Products(2, "Product 2", BigDecimal("20.00"), 1)
        val product3 = Products(3, "Product 3", BigDecimal("30.00"), 1)
        
        // When
        queueViewModel.addSelectedProduct(product1, 1, BigDecimal.ZERO)
        queueViewModel.addSelectedProduct(product2, 2, BigDecimal("5.00"))
        queueViewModel.addSelectedProduct(product3, 3, BigDecimal("10.00"))
        
        // Then
        val selectedProducts = queueViewModel.selectedProducts.first()
        assertEquals(3, selectedProducts.size)
        
        // Calculate total
        val totalPrice = selectedProducts.sumOf { it.totalPrice }
        // Product 1: 10.00 * 1 - 0 = 10.00
        // Product 2: 20.00 * 2 - 5.00 = 35.00
        // Product 3: 30.00 * 3 - 10.00 = 80.00
        // Total: 125.00
        assertEquals(BigDecimal("125.00"), totalPrice)
    }
}
