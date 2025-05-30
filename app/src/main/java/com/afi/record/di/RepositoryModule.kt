package com.afi.record.di

import com.afi.record.domain.repository.CustomerRepo
import com.afi.record.domain.repository.CustomerRepoImpl
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
    abstract fun bindCustomerRepo(
        repoImpl: CustomerRepoImpl
    ): CustomerRepo
}
