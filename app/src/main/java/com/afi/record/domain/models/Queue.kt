package com.afi.record.domain.models

import java.math.BigDecimal

data class OrderItem(
    val productId: Int,
    val quantity: Int,
    val discount: BigDecimal
)

data class CreateQueueRequest(
    val customerId: Int,
    val statusId: Int,
    val paymentId: Int,
    val orders: List<OrderItem>
)

data class UpdateQueueRequest(
    val customerId: Int? = null,
    val statusId: Int? = null,
    val paymentId: Int? = null,
    val orders: List<OrderItem>? = null
)