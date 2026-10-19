package com.example.mviarchitectureapp.ui.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mviarchitectureapp.data.model.Beneficiary
import com.example.mviarchitectureapp.ui.components.BankingTopBar
import com.example.mviarchitectureapp.ui.theme.Teal
import com.example.mviarchitectureapp.ui.util.formatCurrency

@Composable
fun TransferRoute(
    onBack: () -> Unit,
    onTransferSuccess: (String) -> Unit,
    viewModel: TransferViewModel = viewModel(factory = TransferViewModel.Factory()),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TransferEffect.TransferCompleted -> {
                    onTransferSuccess(effect.message)
                    onBack()
                }
            }
        }
    }

    TransferScreen(
        state = state,
        onIntent = viewModel::processIntent,
        onBack = onBack,
    )
}

@Composable
fun TransferScreen(
    state: TransferState,
    onIntent: (TransferIntent) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = { BankingTopBar(title = "Send money", onBack = onBack) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "From account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.accounts.forEach { account ->
                        FilterChip(
                            selected = account.id == state.fromAccountId,
                            onClick = { onIntent(TransferIntent.SelectFromAccount(account.id)) },
                            label = {
                                Text("${account.name} · ${account.balance.formatCurrency()}")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Teal,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item {
                Text(
                    text = "To beneficiary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            items(state.beneficiaries, key = { it.id }) { beneficiary ->
                BeneficiaryCard(
                    beneficiary = beneficiary,
                    selected = beneficiary.id == state.beneficiaryId,
                    onClick = { onIntent(TransferIntent.SelectBeneficiary(beneficiary.id)) },
                )
            }

            item {
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { onIntent(TransferIntent.AmountChanged(it.filter { c -> c.isDigit() || c == '.' })) },
                    label = { Text("Amount (USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                )
            }

            item {
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { onIntent(TransferIntent.NoteChanged(it)) },
                    label = { Text("Note (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                )
            }

            item {
                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                Button(
                    onClick = { onIntent(TransferIntent.SubmitTransfer) },
                    enabled = state.canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal),
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            color = MaterialTheme.colorScheme.onSecondary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Confirm transfer")
                    }
                }
            }
        }
    }
}

@Composable
private fun BeneficiaryCard(
    beneficiary: Beneficiary,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                Teal.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, Teal)
        } else {
            null
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = beneficiary.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${beneficiary.bankName} · ${beneficiary.accountNumberMasked}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
