package com.ivanarroyo.camicalor.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            )
        }
        composable<ProductDetailRoute> {
            ProductDetailScreen(
                onBack = { navController.navigateUp() },
            )
        }
    }
}
