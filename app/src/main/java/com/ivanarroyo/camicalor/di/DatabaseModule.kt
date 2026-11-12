package com.ivanarroyo.camicalor.di

import android.content.Context
import androidx.room.Room
import com.ivanarroyo.camicalor.data.local.CamiCalorDatabase
import com.ivanarroyo.camicalor.data.local.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CamiCalorDatabase =
        Room.databaseBuilder(context, CamiCalorDatabase::class.java, "camicalor.db").build()

    @Provides
    fun provideCartDao(database: CamiCalorDatabase): CartDao = database.cartDao()
}
