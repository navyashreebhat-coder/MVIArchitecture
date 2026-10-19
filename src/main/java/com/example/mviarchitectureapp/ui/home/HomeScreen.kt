package com.example.mviarchitectureapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mviarchitectureapp.ui.theme.NavyDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mviarchitectureapp.data.model.Account
import com.example.mviarchitectureapp.data.model.AccountType
import com.example.mviarchitectureapp.ui.components.BalanceHero
import com.example.mviarchitectureapp.ui.components.BankingTopBar
import com.example.mviarchitectureapp.ui.components.SectionHeader
import com.example.mviarchitectureapp.ui.components.TransactionRow
import com.example.mviarchitectureapp.ui.theme.Navy
import com.example.mviarchitectureapp.ui.theme.Teal
import com.example.mviarchitectureapp.ui.util.formatCurrency

@Composable
fun HomeRoute(
    onNavigateToTransfer: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory()),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToTransfer -> onNavigateToTransfer()
                HomeEffect.NavigateToTransactions -> onNavigateToTransactions()
            }
        }
    }

    HomeScreen(
        state = state,
        onIntent = viewModel::processIntent,
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    Scaffold(
        topBar = { BankingTopBar(title = "Horizon Bank") },
    ) { padding ->
        if (state.isLoading && state.accounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Teal)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                BalanceHero(
                    greeting = "Hello, ${state.user?.name ?: "there"}",
                    balance = state.totalBalance,
                    subtitle = "Total available across checking & savings",
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = { onIntent(HomeIntent.OpenTransfer) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Transfer")
                    }
                    OutlinedButton(
                        onClick = { onIntent(HomeIntent.OpenTransactions) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Navy),
                    ) {
                        Text("All activity", color = Navy)
                    }
                }
            }

            item { SectionHeader(title = "Your accounts") }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.accounts, key = { it.id }) { account ->
                        AccountCard(
                            account = account,
                            selected = account.id == state.selectedAccountId,
                            onClick = { onIntent(HomeIntent.SelectAccount(account.id)) },
                        )
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Recent activity",
                    actionLabel = "See all",
                    onAction = { onIntent(HomeIntent.OpenTransactions) },
                )
            }

            items(state.recentTransactions, key = { it.id }) { transaction ->
                TransactionRow(transaction)
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
        }
    }
}

@Composable
private fun AccountCard(
    account: Account,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(260.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Teal else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 1.dp),
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = account.type.icon(),
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.onSecondary else Navy,
                )
                Text(
                    text = account.accountNumberMasked,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.9f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            Column {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) MaterialTheme.colorScheme.onSecondary else Navy,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = account.balance.formatCurrency(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (selected) MaterialTheme.colorScheme.onSecondary else NavyDark,
                )
            }
        }
    }
}

private fun AccountType.icon(): ImageVector = when (this) {
    AccountType.CHECKING -> Icons.Default.AccountBalanceWallet
    AccountType.SAVINGS -> Icons.Default.Savings
    AccountType.CREDIT -> Icons.Default.CreditCard
}
