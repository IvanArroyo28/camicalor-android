package com.ivanarroyo.camicalor.presentation.navigation

import kotlinx.serialization.Serializable

/** Rutas type-safe: el compilador verifica los argumentos, no hay strings como "detail/{id}". */
@Serializable
data object CatalogRoute

@Serializable
data class ProductDetailRoute(val productId: String)
