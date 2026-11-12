package com.ivanarroyo.camicalor.presentation.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivanarroyo.camicalor.R
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.model.Order
import com.ivanarroyo.camicalor.domain.model.OrderTotals
import com.ivanarroyo.camicalor.domain.model.PaymentMethod
import com.ivanarroyo.camicalor.domain.model.ShippingError
import com.ivanarroyo.camicalor.domain.model.ShippingField
import com.ivanarroyo.camicalor.domain.model.ShippingInfo
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.presentation.util.toCopPrice
import com.ivanarroyo.camicalor.ui.theme.CamicalorTheme

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderPlaced: (Order) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CheckoutEvent.OrderPlaced -> onOrderPlaced(event.order)
                CheckoutEvent.OrderFailed -> snackbarHostState.showSnackbar(context.getString(R.string.checkout_error))
            }
        }
    }

    CheckoutContent(
        summary = summary,
        form = viewModel.form,
        errors = viewModel.visibleErrors,
        paymentMethod = viewModel.paymentMethod,
        isFormValid = viewModel.isFormValid,
        isPlacingOrder = viewModel.isPlacingOrder,
        onFieldChange = viewModel::onFieldChange,
        onPaymentMethodSelected = viewModel::onPaymentMethodSelected,
        onConfirm = viewModel::onConfirm,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    summary: CheckoutSummary?,
    form: ShippingInfo,
    errors: Map<ShippingField, ShippingError>,
    paymentMethod: PaymentMethod,
    isFormValid: Boolean,
    isPlacingOrder: Boolean,
    onFieldChange: (ShippingField, String) -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checkout_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isPlacingOrder) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
        bottomBar = {
            if (summary != null && summary.items.isNotEmpty()) {
                ConfirmBar(
                    total = summary.totals.total,
                    enabled = isFormValid && !isPlacingOrder,
                    isPlacingOrder = isPlacingOrder,
                    onConfirm = onConfirm,
                )
            }
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
            // Cargando, o el carrito se acaba de vaciar porque el pedido se confirmó.
            summary == null || (summary.items.isEmpty() && isPlacingOrder) ->
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

            summary.items.isEmpty() -> Column(
                modifier = modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(R.string.cart_empty), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onBack) { Text(stringResource(R.string.back)) }
            }

            else -> Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OrderSummaryCard(summary)
                ShippingForm(form = form, errors = errors, enabled = !isPlacingOrder, onFieldChange = onFieldChange)
                PaymentMethodSelector(selected = paymentMethod, enabled = !isPlacingOrder, onSelected = onPaymentMethodSelected)
                Text(
                    text = stringResource(R.string.checkout_simulated_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(summary: CheckoutSummary) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.checkout_summary), style = MaterialTheme.typography.titleMedium)
            summary.items.forEach { item ->
                SummaryRow(
                    label = stringResource(R.string.checkout_item_line, item.quantity, item.name, item.size.code),
                    value = item.subtotal.toCopPrice(),
                )
            }
            HorizontalDivider()
            SummaryRow(stringResource(R.string.cart_subtotal), summary.totals.subtotal.toCopPrice())
            SummaryRow(
                label = stringResource(R.string.checkout_shipping),
                value = if (summary.totals.isFreeShipping) stringResource(R.string.checkout_free)
                else summary.totals.shippingCost.toCopPrice(),
                highlight = summary.totals.isFreeShipping,
            )
            SummaryRow(stringResource(R.string.checkout_total), summary.totals.total.toCopPrice(), bold = true)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false, highlight: Boolean = false) {
    val style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = style, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            style = style,
            fontWeight = if (bold) FontWeight.Bold else null,
            color = if (highlight || bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ShippingForm(
    form: ShippingInfo,
    errors: Map<ShippingField, ShippingError>,
    enabled: Boolean,
    onFieldChange: (ShippingField, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.checkout_shipping_data), style = MaterialTheme.typography.titleMedium)
        FormField(
            value = form.fullName, label = R.string.checkout_full_name, field = ShippingField.FULL_NAME,
            errors = errors, enabled = enabled, onFieldChange = onFieldChange,
        )
        FormField(
            value = form.phone, label = R.string.checkout_phone, field = ShippingField.PHONE,
            errors = errors, enabled = enabled, onFieldChange = onFieldChange,
            keyboardType = KeyboardType.Phone, capitalization = KeyboardCapitalization.None,
        )
        FormField(
            value = form.address, label = R.string.checkout_address, field = ShippingField.ADDRESS,
            errors = errors, enabled = enabled, onFieldChange = onFieldChange,
            capitalization = KeyboardCapitalization.Sentences,
        )
        FormField(
            value = form.neighborhood, label = R.string.checkout_neighborhood, field = ShippingField.NEIGHBORHOOD,
            errors = errors, enabled = enabled, onFieldChange = onFieldChange,
        )
        FormField(
            value = form.city, label = R.string.checkout_city, field = ShippingField.CITY,
            errors = errors, enabled = enabled, onFieldChange = onFieldChange, imeAction = ImeAction.Done,
        )
    }
}

@Composable
private fun FormField(
    value: String,
    label: Int,
    field: ShippingField,
    errors: Map<ShippingField, ShippingError>,
    enabled: Boolean,
    onFieldChange: (ShippingField, String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Words,
    imeAction: ImeAction = ImeAction.Next,
) {
    val errorText = errors[field]?.let { stringResource(errorMessageRes(field, it)) }
    OutlinedTextField(
        value = value,
        onValueChange = { onFieldChange(field, it) },
        label = { Text(stringResource(label)) },
        isError = errorText != null,
        supportingText = errorText?.let { text -> { Text(text) } },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun errorMessageRes(field: ShippingField, error: ShippingError): Int = when (error) {
    ShippingError.REQUIRED -> R.string.error_required
    ShippingError.INVALID_PHONE -> R.string.error_invalid_phone
    ShippingError.TOO_SHORT ->
        if (field == ShippingField.ADDRESS) R.string.error_address_too_short else R.string.error_name_too_short
}

@Composable
private fun PaymentMethodSelector(
    selected: PaymentMethod,
    enabled: Boolean,
    onSelected: (PaymentMethod) -> Unit,
) {
    Column {
        Text(stringResource(R.string.checkout_payment_method), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Column(Modifier.selectableGroup()) {
            PaymentMethod.entries.forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = method == selected,
                            enabled = enabled,
                            role = Role.RadioButton,
                            onClick = { onSelected(method) },
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // onClick = null: toda la fila es la que se puede tocar (mejor para accesibilidad).
                    RadioButton(selected = method == selected, onClick = null, enabled = enabled)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(method.titleRes()), style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = stringResource(method.descriptionRes()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmBar(total: Long, enabled: Boolean, isPlacingOrder: Boolean, onConfirm: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.checkout_total), style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = total.toCopPrice(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Button(onClick = onConfirm, enabled = enabled) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(R.string.checkout_confirm))
                }
            }
        }
    }
}

// ---------- Previews ----------

private val previewSummary = CheckoutSummary(
    items = listOf(
        CartItem("cc-001", "Camiseta Sol Caribe", "", 59900, Size.M, 2),
        CartItem("cc-009", "Oversize Bahía", "", 74900, Size.L, 1),
    ),
    totals = OrderTotals(subtotal = 194700, shippingCost = 0),
)

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun CheckoutPreview() {
    CamicalorTheme {
        CheckoutContent(
            summary = previewSummary,
            form = ShippingInfo(fullName = "Iv", phone = "300123"),
            errors = mapOf(ShippingField.FULL_NAME to ShippingError.TOO_SHORT, ShippingField.PHONE to ShippingError.INVALID_PHONE),
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            isFormValid = false,
            isPlacingOrder = false,
            onFieldChange = { _, _ -> }, onPaymentMethodSelected = {}, onConfirm = {}, onBack = {},
        )
    }
}
