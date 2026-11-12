package com.ivanarroyo.camicalor.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ivanarroyo.camicalor.domain.model.PaymentMethod
import com.ivanarroyo.camicalor.presentation.cart.CartScreen
import com.ivanarroyo.camicalor.presentation.catalog.CatalogScreen
import com.ivanarroyo.camicalor.presentation.checkout.CheckoutScreen
import com.ivanarroyo.camicalor.presentation.confirmation.OrderConfirmationScreen
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
                onCheckout = { navController.navigate(CheckoutRoute) },
            )
        }
        composable<CheckoutRoute> {
            CheckoutScreen(
                onBack = { navController.navigateUp() },
                onOrderPlaced = { order ->
                    navController.navigate(
                        OrderConfirmationRoute(
                            orderId = order.id,
                            customerName = order.shippingInfo.fullName,
                            total = order.totals.total,
                            itemCount = order.itemCount,
                            paymentMethod = order.paymentMethod.name,
                        )
                    ) {
                        // Se sacan del historial el detalle, el carrito y el checkout:
                        // al presionar "atrás" desde la confirmación se vuelve al catálogo.
                        popUpTo(CatalogRoute) { inclusive = false }
                    }
                },
            )
        }
        composable<OrderConfirmationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<OrderConfirmationRoute>()
            OrderConfirmationScreen(
                orderId = route.orderId,
                customerName = route.customerName,
                total = route.total,
                itemCount = route.itemCount,
                paymentMethod = PaymentMethod.entries.firstOrNull { it.name == route.paymentMethod }
                    ?: PaymentMethod.CASH_ON_DELIVERY,
                onContinueShopping = { navController.popBackStack(CatalogRoute, inclusive = false) },
            )
        }
    }
}
