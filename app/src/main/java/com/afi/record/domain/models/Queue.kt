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
    val paymentId: Int? = null,
    val note: String? = null,
    val orders: List<OrderItem>
)

data class UpdateQueueRequest(
    val customerId: Int? = null,
    val statusId: Int? = null,
    val paymentId: Int? = null,
    val orders: List<OrderItem>? = null
)

data class QueueResponse(
    val data: List<DataItem>? = null
)

data class OrdersItem(
    val product: String? = null,
    val quantity: Int? = null,
    val totalPrice: String? = null,
    val discount: String? = null
)

data class DataItem(
    val note: String? = null,
    val grandTotal: Int? = null,
    val payment: Any? = null,
    val orders: List<OrdersItem>? = null,
    val id: Int? = null,
    val user: String? = null,
    val customer: String? = null,
    val status: String? = null
)