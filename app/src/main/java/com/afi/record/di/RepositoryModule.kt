package com.afi.record.di

import com.afi.record.data.repositoryImpl.AuthRepoImpl
import com.afi.record.data.repositoryImpl.CustomerRepoImpl
import com.afi.record.data.repositoryImpl.ProductRepoImpl
import com.afi.record.data.repositoryImpl.QueueRepoImpl
import com.afi.record.domain.repository.AuthRepo
import com.afi.record.domain.repository.CustomerRepo
import com.afi.record.domain.repository.ProductRepo
import com.afi.record.domain.repository.QueueRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepo(
        repoImpl: AuthRepoImpl
    ): AuthRepo

    @Binds
    @Singleton
    abstract fun bindCustomerRepo(
        repoImpl: CustomerRepoImpl
    ): CustomerRepo

    @Binds
    @Singleton
    abstract fun bindProductRepo(
        repoImpl: ProductRepoImpl
    ): ProductRepo

    @Binds
    @Singleton
    abstract fun bindQueueRepo(
        repoImpl: QueueRepoImpl
    ): QueueRepo
}
