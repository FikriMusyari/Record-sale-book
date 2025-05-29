package com.afi.record.domain.repository

import com.afi.record.domain.models.Customers


interface CustomerRepo {

    suspend fun searchcustomers(query: String) : List<Customers>
    suspend fun getAllCustomers(): List<Customers>
}