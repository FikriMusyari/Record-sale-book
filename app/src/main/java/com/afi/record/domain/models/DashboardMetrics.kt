package com.afi.record.domain.models

import java.math.BigDecimal

data class DashboardMetrics(
    val totalQueues: Int = 0,
    val uncompletedQueues: Int = 0,
    val activeCustomers: Int = 0,
    val productsSold: Int = 0,
    val revenue: BigDecimal = BigDecimal.ZERO
)