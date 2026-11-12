package com.ivanarroyo.camicalor.di

import com.ivanarroyo.camicalor.data.order.RandomOrderNumberGenerator
import com.ivanarroyo.camicalor.domain.service.OrderNumberGenerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckoutModule {

    @Binds
    abstract fun bindOrderNumberGenerator(impl: RandomOrderNumberGenerator): OrderNumberGenerator
}
