package com.afi.record.data.repositoryImpl

import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.CreateCustomersRequest
import com.afi.record.domain.models.CustomersResponse
import com.afi.record.domain.models.CustomersSearchResponse
import com.afi.record.domain.models.UpdateCustomersRequest
import com.afi.record.domain.repository.CustomerRepo
import javax.inject.Inject

class CustomerRepoImpl @Inject constructor(
    private val api: ApiService
) : CustomerRepo {
    override suspend fun getAllCustomers(): CustomersResponse = api.getAllCustomers()

    override suspend fun searchcustomers(query: String): CustomersSearchResponse =
        api.searchcustomers(query)

    override suspend fun createCustomer(request: CreateCustomersRequest) =
        api.createCustomers(request)

    override suspend fun updateCustomer(customerId: Number, request: UpdateCustomersRequest) =
        api.updateCustomers(customerId, request)

    override suspend fun deleteCustomer(customerId: Number) =
        api.deleteCustomer(customerId)
}