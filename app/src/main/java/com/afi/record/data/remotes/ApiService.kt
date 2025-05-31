package com.afi.record.data.remotes

import com.afi.record.domain.models.CreateCustomersRequest
import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.CreateQueueRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.DataUserResponse
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.ProductResponse
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.ProductsSearchResponse
import com.afi.record.domain.models.QueueResponse
import com.afi.record.domain.models.UpdateCustomersRequest
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.domain.models.UpdateQueueRequest
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.models.Users
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // User
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): DataUserResponse

    @POST("api/users")
    suspend fun register(@Body user: Users): UserResponse

    @GET("api/users/current")
    suspend fun getUserCurrent(): DataUserResponse

    @PATCH("api/users/current")
    suspend fun UpdateCurrentUser(@Body request: UpdateUserRequest) : UserResponse

    @DELETE("api/users/logout")
    suspend fun logout()

    // Product
    @POST("api/products")
    suspend fun createProduct(@Body request: CreateProductRequest): Products

    @GET("api/products")
    suspend fun getAllProducts(): ProductResponse

    @GET("api/products/search")
    suspend fun searchproducts(@Query("nama") query: String): ProductsSearchResponse

    @PUT("api/products/{productId}")
    suspend fun updateProduct(@Path("productId") productId: Number, @Body request: UpdateProductRequest): Products

    @DELETE("api/products/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: Number)

    // Customers
    @POST("api/customers")
    suspend fun createCustomers(@Body request: CreateCustomersRequest)

    @PUT("api/customers/{customerId}")
    suspend fun updateCustomers(@Path("customerId") customerId: Number, @Body request: UpdateCustomersRequest)

    @GET("api/customers")
    suspend fun getAllCustomers(): List<Customers>

    @GET("api/customers/search")
    suspend fun searchcustomers(@Query("nama") query: String): List<Customers>

    @DELETE("api/customers/{customerId}")
    suspend fun deleteCustomer(@Path("customerId") customerId: Number)

    // Queue
    @POST("api/queue")
    suspend fun createQueue(@Body request: CreateQueueRequest): QueueResponse

    @GET("api/queue")
    suspend fun getAllQueue(): QueueResponse

    @PUT("api/queue/{queueId}")
    suspend fun updateQueue(@Path("queueId") queueId: Number, @Body request: UpdateQueueRequest)

    @DELETE("api/queue/{queueId}")
    suspend fun deleteQueue(@Path("queueId") queueId: Number)


}