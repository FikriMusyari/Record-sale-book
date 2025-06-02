package com.afi.record.domain.models

import java.math.BigDecimal

data class Customers(
    val id: Int,
    val nama: String,
    val balance: BigDecimal,
    val userId: Int
)

data class CreateCustomersRequest(
    val nama: String,
    val balance: BigDecimal
)

data class UpdateCustomersRequest(
    val nama: String?,
    val balance: BigDecimal?,
)

data class CustomersResponse(
    val data: List<Customers>
)

data class CustomersSearchResponse(
    val data: List<Customers> ? = null
)