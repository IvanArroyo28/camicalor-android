package com.ivanarroyo.camicalor.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivanarroyo.camicalor.presentation.cart.CartScreen
import com.ivanarroyo.camicalor.presentation.catalog.CatalogScreen
import com.ivanarroyo.camicalor.presentation.detail.ProductDetailScreen

@Composable
fun CamiCalorNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = CatalogRoute) {
        composable<CatalogRoute> {
            CatalogScreen(
                onProductClick = { productId -> navController.navigate(ProductDetailRoute(productId)) },
                onOpenCart = { navController.navigate(CartRoute) },
            )
        }
        composable<ProductDetailRoute> {
            ProductDetailScreen(
                onBack = { navController.navigateUp() },
                onOpenCart = { navController.navigate(CartRoute) },
            )
        }
        composable<CartRoute> {
            CartScreen(
                onBack = { navController.navigateUp() },
                // Desde el carrito vacío, "Ver camisetas" vuelve al catálogo limpiando lo que hubiera en medio.
                onBrowseProducts = { navController.popBackStack(CatalogRoute, inclusive = false) },
            )
        }
    }
}
