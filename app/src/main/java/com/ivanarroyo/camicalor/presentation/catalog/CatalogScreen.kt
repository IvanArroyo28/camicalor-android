package com.ivanarroyo.camicalor.presentation.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
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
import com.ivanarroyo.camicalor.presentation.util.toCopPrice
import com.ivanarroyo.camicalor.ui.theme.CamicalorTheme

/** Punto de entrada: conecta el ViewModel con la UI. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onProductClick: (productId: String) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { innerPadding ->
        CatalogContent(
            uiState = uiState,
            onRetry = viewModel::loadProducts,
            onProductClick = onProductClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/** UI pura: recibe el estado y lo dibuja. Así se puede previsualizar y testear sin ViewModel. */
@Composable
fun CatalogContent(
    uiState: CatalogUiState,
    onRetry: () -> Unit,
    onProductClick: (productId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        CatalogUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is CatalogUiState.Error -> ErrorState(
            message = stringResource(
                if (uiState.isNetworkError) R.string.catalog_error_network else R.string.catalog_error_unknown
            ),
            onRetry = onRetry,
            modifier = modifier,
        )

        is CatalogUiState.Success ->
            if (uiState.products.isEmpty()) {
                Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.catalog_empty), textAlign = TextAlign.Center)
                }
            } else {
                ProductGrid(products = uiState.products, onProductClick = onProductClick, modifier = modifier)
            }
    }
}

@Composable
private fun ProductGrid(
    products: List<Product>,
    onProductClick: (productId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(product = product, onClick = { onProductClick(product.id) })
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Box {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .alpha(if (product.inStock) 1f else 0.5f),
            )
            if (!product.inStock) {
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.out_of_stock),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
        Column(Modifier.padding(12.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.price.toCopPrice(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}

// ---------- Previews ----------

private val previewProducts = listOf(
    Product("cc-001", "Camiseta Sol Caribe", "", 59900, Category.PRINTED, listOf(Size.M), "", true),
    Product("cc-006", "Básica Azul Mar", "", 36900, Category.BASIC, listOf(Size.L), "", false),
)

@Preview(showBackground = true)
@Composable
private fun CatalogSuccessPreview() {
    CamicalorTheme { CatalogContent(CatalogUiState.Success(previewProducts), onRetry = {}, onProductClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun CatalogErrorPreview() {
    CamicalorTheme { CatalogContent(CatalogUiState.Error(isNetworkError = true), onRetry = {}, onProductClick = {}) }
}
