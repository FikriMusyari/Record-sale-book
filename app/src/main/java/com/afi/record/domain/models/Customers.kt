package com.afi.record.domain.models

import java.math.BigDecimal

data class Customers(
    val id: String,
    val nama: String,
    val balance: String,
    val userId: String
)

data class CreateCustomersRequest(
    val nama: String,
    val balance: BigDecimal
)

data class UpdateCustomersRequest(
    val nama: String?,
    val balance: BigDecimal?,
)
