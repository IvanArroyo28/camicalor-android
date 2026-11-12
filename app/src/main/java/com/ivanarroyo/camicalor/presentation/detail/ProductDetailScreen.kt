package com.ivanarroyo.camicalor.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ivanarroyo.camicalor.R
import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.presentation.detail.ProductDetailUiState.ErrorReason
import com.ivanarroyo.camicalor.presentation.util.toCopPrice
import com.ivanarroyo.camicalor.ui.theme.CamicalorTheme

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Escucha los eventos de una sola vez del ViewModel y muestra el snackbar.
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductDetailEvent.AddedToCart -> {
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.detail_added_to_cart, event.size.code),
                        actionLabel = context.getString(R.string.detail_view_cart),
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) onOpenCart()
                }
                ProductDetailEvent.AddToCartFailed ->
                    snackbarHostState.showSnackbar(context.getString(R.string.detail_add_error))
            }
        }
    }

    ProductDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadProduct,
        onSizeSelected = viewModel::onSizeSelected,
        onAddToCart = viewModel::onAddToCart,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    uiState: ProductDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSizeSelected: (Size) -> Unit,
    onAddToCart: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is ProductDetailUiState.Success) {
                        Text(uiState.product.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (uiState is ProductDetailUiState.Success && uiState.product.inStock) {
                AddToCartBar(enabled = uiState.canAddToCart, onAddToCart = onAddToCart)
            }
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (uiState) {
            ProductDetailUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is ProductDetailUiState.Error -> ErrorState(
                message = stringResource(
                    when (uiState.reason) {
                        ErrorReason.NETWORK -> R.string.catalog_error_network
                        ErrorReason.NOT_FOUND -> R.string.detail_error_not_found
                        ErrorReason.UNKNOWN -> R.string.catalog_error_unknown
                    }
                ),
                // Si el producto no existe, reintentar no sirve de nada.
                onRetry = onRetry.takeIf { uiState.reason != ErrorReason.NOT_FOUND },
                modifier = modifier,
            )

            is ProductDetailUiState.Success -> ProductDetail(
                product = uiState.product,
                selectedSize = uiState.selectedSize,
                onSizeSelected = onSizeSelected,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ProductDetail(
    product: Product,
    selectedSize: Size?,
    onSizeSelected: (Size) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        )
        Column(Modifier.padding(16.dp)) {
            Text(
                text = product.category.displayName.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(product.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = product.price.toCopPrice(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(16.dp))
            Text(product.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))

            if (product.inStock) {
                Text(stringResource(R.string.detail_size), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.sizes.forEach { size ->
                        FilterChip(
                            selected = size == selectedSize,
                            onClick = { onSizeSelected(size) },
                            label = { Text(size.code) },
                        )
                    }
                }
                if (selectedSize == null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.detail_select_size_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.detail_out_of_stock_message),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun AddToCartBar(enabled: Boolean, onAddToCart: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Button(
            onClick = onAddToCart,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.detail_add_to_cart))
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: (() -> Unit)?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, textAlign = TextAlign.Center)
        if (onRetry != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
        }
    }
}

// ---------- Previews ----------

private val previewProduct = Product(
    id = "cc-001",
    name = "Camiseta Sol Caribe",
    description = "Camiseta estampada con un sol al atardecer sobre el mar Caribe. Algodón 100 %, ideal para el calor.",
    price = 59900,
    category = Category.PRINTED,
    sizes = listOf(Size.S, Size.M, Size.L, Size.XL),
    imageUrl = "",
    inStock = true,
)

@Preview(showBackground = true)
@Composable
private fun DetailWithSizePreview() {
    CamicalorTheme {
        ProductDetailContent(
            uiState = ProductDetailUiState.Success(previewProduct, selectedSize = Size.M),
            onBack = {}, onRetry = {}, onSizeSelected = {}, onAddToCart = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailOutOfStockPreview() {
    CamicalorTheme {
        ProductDetailContent(
            uiState = ProductDetailUiState.Success(previewProduct.copy(inStock = false)),
            onBack = {}, onRetry = {}, onSizeSelected = {}, onAddToCart = {},
        )
    }
}
