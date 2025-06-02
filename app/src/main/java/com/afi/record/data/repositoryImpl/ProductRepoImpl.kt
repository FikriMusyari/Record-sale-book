package com.afi.record.data.repositoryImpl

import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.ProductResponse
import com.afi.record.domain.models.ProductsSearchResponse
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.domain.repository.ProductRepo
import javax.inject.Inject


class ProductRepoImpl @Inject constructor(
    private val api: ApiService
) : ProductRepo {
    override suspend fun getAllProducts(): ProductResponse = api.getAllProducts()

    override suspend fun searchproducts(query: String): ProductsSearchResponse =
        api.searchproducts(query)

    override suspend fun createProduct(request: CreateProductRequest) =
        api.createProduct(request)

    override suspend fun updateProduct(productId: Number, request: UpdateProductRequest) =
        api.updateProduct(productId, request)

    override suspend fun deleteProduct(productId: Number) =
        api.deleteProduct(productId)
}