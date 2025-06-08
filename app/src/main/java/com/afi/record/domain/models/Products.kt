package com.afi.record.domain.models

import java.math.BigDecimal

data class Products(
    val id: Int,
    val nama: String,
    val price: BigDecimal,
    val userId: Int
)

data class ProductsSearchResponse(
    val data: List<Products> ? = null
)

data class CreateProductRequest(
    val nama: String,
    val price: BigDecimal
)

data class UpdateProductRequest(
     val nama: String?,
     val price: BigDecimal?,
 )

data class ProductResponse(
    val data: List<Products>
)

