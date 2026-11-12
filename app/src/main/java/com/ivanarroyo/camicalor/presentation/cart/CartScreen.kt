package com.ivanarroyo.camicalor.presentation.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ivanarroyo.camicalor.R
import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.model.ShippingPolicy
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.presentation.util.toCopPrice
import com.ivanarroyo.camicalor.ui.theme.CamicalorTheme

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onBrowseProducts: () -> Unit,
    onCheckout: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CartContent(
        uiState = uiState,
        onBack = onBack,
        onBrowseProducts = onBrowseProducts,
        onIncrease = viewModel::onIncrease,
        onDecrease = viewModel::onDecrease,
        onRemove = viewModel::onRemove,
        onCheckout = onCheckout,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(
    uiState: CartUiState,
    onBack: () -> Unit,
    onBrowseProducts: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
    onCheckout: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cart_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
        bottomBar = {
            if (uiState is CartUiState.Content && !uiState.cart.isEmpty) {
                CheckoutBar(cart = uiState.cart, onCheckout = onCheckout)
            }
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (uiState) {
            CartUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is CartUiState.Content ->
                if (uiState.cart.isEmpty) {
                    EmptyCart(onBrowseProducts = onBrowseProducts, modifier = modifier)
                } else {
                    LazyColumn(
                        modifier = modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.cart.items, key = { "${it.productId}-${it.size.code}" }) { item ->
                            CartItemRow(
                                item = item,
                                onIncrease = { onIncrease(item) },
                                onDecrease = { onDecrease(item) },
                                onRemove = { onRemove(item) },
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    text = stringResource(R.string.cart_size, item.size.code),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.subtotal.toCopPrice(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                QuantityStepper(
                    quantity = item.quantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease,
                    canIncrease = item.quantity < Cart.MAX_QUANTITY_PER_ITEM,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.cart_remove))
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    canIncrease: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onDecrease, enabled = quantity > 1) {
            Icon(painterResource(R.drawable.ic_remove), contentDescription = stringResource(R.string.cart_decrease))
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp),
        )
        IconButton(onClick = onIncrease, enabled = canIncrease) {
            Icon(painterResource(R.drawable.ic_add), contentDescription = stringResource(R.string.cart_increase))
        }
    }
}

@Composable
private fun CheckoutBar(cart: Cart, onCheckout: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.cart_subtotal), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = pluralStringResource(R.plurals.cart_items_count, cart.totalItems, cart.totalItems),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = cart.total.toCopPrice(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(8.dp))
            FreeShippingHint(subtotal = cart.total)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cart_checkout))
            }
        }
    }
}

/** Motiva a completar el pedido: "Te faltan $ 30.000 para el envío gratis". */
@Composable
private fun FreeShippingHint(subtotal: Long) {
    val remaining = ShippingPolicy.remainingForFreeShipping(subtotal)
    Text(
        text = if (remaining > 0) {
            stringResource(R.string.cart_free_shipping_remaining, remaining.toCopPrice())
        } else {
            stringResource(R.string.cart_free_shipping_reached)
        },
        style = MaterialTheme.typography.bodySmall,
        color = if (remaining > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun EmptyCart(onBrowseProducts: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cart),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.cart_empty), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBrowseProducts) { Text(stringResource(R.string.cart_browse)) }
    }
}

// ---------- Previews ----------

private val previewCart = Cart(
    listOf(
        CartItem("cc-001", "Camiseta Sol Caribe", "", 59900, Size.M, 2),
        CartItem("cc-009", "Oversize Bahía", "", 74900, Size.L, 1),
    )
)

@Preview(showBackground = true)
@Composable
private fun CartWithItemsPreview() {
    CamicalorTheme {
        CartContent(CartUiState.Content(previewCart), {}, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun CartEmptyPreview() {
    CamicalorTheme {
        CartContent(CartUiState.Content(Cart()), {}, {}, {}, {}, {}, {})
    }
}
