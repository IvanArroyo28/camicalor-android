package com.ivanarroyo.camicalor.presentation.confirmation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivanarroyo.camicalor.R
import com.ivanarroyo.camicalor.domain.model.PaymentMethod
import com.ivanarroyo.camicalor.presentation.checkout.titleRes
import com.ivanarroyo.camicalor.presentation.util.toCopPrice
import com.ivanarroyo.camicalor.ui.theme.CamicalorTheme

/** Pantalla sin ViewModel: todo lo que muestra llega por la ruta de navegación. */
@Composable
fun OrderConfirmationScreen(
    orderId: String,
    customerName: String,
    total: Long,
    itemCount: Int,
    paymentMethod: PaymentMethod,
    onContinueShopping: () -> Unit,
) {
    val firstName = customerName.trim().substringBefore(' ')
    val formattedTotal = total.toCopPrice()

    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check_circle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.confirmation_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.confirmation_thanks, firstName),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailRow(stringResource(R.string.confirmation_order_number), orderId, bold = true)
                    HorizontalDivider()
                    DetailRow(
                        stringResource(R.string.confirmation_items),
                        pluralStringResource(R.plurals.cart_items_count, itemCount, itemCount),
                    )
                    DetailRow(stringResource(R.string.confirmation_payment), stringResource(paymentMethod.titleRes()))
                    DetailRow(stringResource(R.string.confirmation_total), formattedTotal, bold = true)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(
                    when (paymentMethod) {
                        PaymentMethod.CASH_ON_DELIVERY -> R.string.confirmation_note_cash
                        PaymentMethod.BANK_TRANSFER -> R.string.confirmation_note_transfer
                    },
                    formattedTotal,
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onContinueShopping, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.confirmation_continue))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else null,
            color = if (bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderConfirmationPreview() {
    CamicalorTheme {
        OrderConfirmationScreen(
            orderId = "CC-4821",
            customerName = "Ivan Arroyo",
            total = 194700,
            itemCount = 3,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            onContinueShopping = {},
        )
    }
}
