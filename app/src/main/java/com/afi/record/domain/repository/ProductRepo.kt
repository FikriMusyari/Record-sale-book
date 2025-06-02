package com.afi.record.domain.repository

import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.ProductResponse
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.ProductsSearchResponse
import com.afi.record.domain.models.UpdateProductRequest


interface ProductRepo {
    suspend fun getAllProducts(): ProductResponse
    suspend fun searchproducts(query: String): ProductsSearchResponse
    suspend fun createProduct(request: CreateProductRequest): Products
    suspend fun updateProduct(productId: Number, request: UpdateProductRequest): Products
    suspend fun deleteProduct(productId: Number)
}