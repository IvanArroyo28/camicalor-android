package com.ivanarroyo.camicalor.di

import com.ivanarroyo.camicalor.data.repository.CartRepositoryImpl
import com.ivanarroyo.camicalor.data.repository.ProductRepositoryImpl
import com.ivanarroyo.camicalor.domain.repository.CartRepository
import com.ivanarroyo.camicalor.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /** Cuando alguien pida un ProductRepository, Hilt entrega ProductRepositoryImpl. */
    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
}
