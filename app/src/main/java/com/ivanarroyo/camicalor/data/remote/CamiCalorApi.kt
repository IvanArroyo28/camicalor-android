package com.ivanarroyo.camicalor.data.remote

import com.ivanarroyo.camicalor.data.remote.dto.ProductsResponseDto
import retrofit2.http.GET

interface CamiCalorApi {

    @GET("api/products.json")
    suspend fun getProducts(): ProductsResponseDto

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/IvanArroyo28/camicalor-android/main/"
    }
}
