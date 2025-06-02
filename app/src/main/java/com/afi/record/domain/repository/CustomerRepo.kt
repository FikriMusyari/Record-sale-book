package com.afi.record.domain.repository

import com.afi.record.domain.models.CreateCustomersRequest
import com.afi.record.domain.models.CustomersResponse
import com.afi.record.domain.models.CustomersSearchResponse
import com.afi.record.domain.models.UpdateCustomersRequest

interface CustomerRepo {
    suspend fun getAllCustomers(): CustomersResponse
    suspend fun searchcustomers(query: String): CustomersSearchResponse
    suspend fun createCustomer(request: CreateCustomersRequest)
    suspend fun updateCustomer(customerId: Number, request: UpdateCustomersRequest)
    suspend fun deleteCustomer(customerId: Number)
}
