package com.afi.record.domain.repository

import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.CreateCustomersRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.UpdateCustomersRequest
import javax.inject.Inject

class CustomerRepoImpl @Inject constructor(
    private val api: ApiService
) : CustomerRepo {
    override suspend fun getAllCustomers(): List<Customers> = api.getAllCustomers()

    override suspend fun searchcustomers(query: String): List<Customers> =
        api.searchcustomers(query)

    override suspend fun createCustomer(request: CreateCustomersRequest) =
        api.createCustomers(request)

    override suspend fun updateCustomer(customerId: String, request: UpdateCustomersRequest) =
        api.updateCustomers(customerId, request)

    override suspend fun deleteCustomer(customerId: String) =
        api.deleteCustomer(customerId)
}
