package com.afi.record.domain.repository

import com.afi.record.domain.models.CreateCustomersRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.UpdateCustomersRequest

interface CustomerRepo {
    suspend fun getAllCustomers(): List<Customers>
    suspend fun searchcustomers(query: String): List<Customers>
    suspend fun createCustomer(request: CreateCustomersRequest)
    suspend fun updateCustomer(customerId: Number, request: UpdateCustomersRequest)
    suspend fun deleteCustomer(customerId: Number)
}
