package com.afi.record.domain.models

import java.math.BigDecimal

data class Products(
    val id: Int,
    val nama: String,
    val price: String,
    val userId: Number
)

data class ProductsSearchResponse(
    val data: List<Products> ? = null
)

data class CreateProductRequest(
    val nama: String,
    val price: Number
)

data class UpdateProductRequest(
     val nama: String?,
     val price: BigDecimal?,
 )

