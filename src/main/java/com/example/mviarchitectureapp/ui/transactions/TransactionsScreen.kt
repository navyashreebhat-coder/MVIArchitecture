package com.example.mviarchitectureapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mviarchitectureapp.ui.components.BankingTopBar
import com.example.mviarchitectureapp.ui.components.TransactionRow
import com.example.mviarchitectureapp.ui.theme.Teal

@Composable
fun TransactionsRoute(
    onBack: () -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = TransactionsViewModel.Factory()),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TransactionsScreen(state = state, onBack = onBack)
}

@Composable
fun TransactionsScreen(
    state: TransactionsState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = { BankingTopBar(title = "All activity", onBack = onBack) },
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Teal)
                }
            }

            state.transactions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No transactions yet")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionRow(transaction)
                    }
                }
            }
        }
    }
}
