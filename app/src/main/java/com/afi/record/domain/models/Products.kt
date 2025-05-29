package com.afi.record.domain.models

data class Products(
    val id: Int,
    val nama: String,
    val price: String,
    val userId: Int
)

data class ProductsSearchResponse(
    val data: List<Products> ? = null
)

data class CreateProductRequest(
    val nama: String,
    val price: Number
)

data class UpdateProductRequest(
    val id: Int,
     val nama: String?,
     val price: Number?,
 )

data class ProductResponse(
    val data: List<Products>
)

