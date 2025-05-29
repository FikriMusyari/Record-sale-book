package com.afi.record.domain.repository

import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.domain.useCase.AuthResult
import kotlinx.coroutines.flow.Flow

interface ProductRepo {

    suspend fun searchproduct(query: String) : List<Products>
    suspend fun getAllProduct(): List<Products>

    fun createProduct(request: CreateProductRequest): Flow<AuthResult>
    fun updateProduct(productId: Number, request: UpdateProductRequest): Flow<AuthResult>
    fun deleteProduct(productId: Number): Flow<AuthResult>
}
