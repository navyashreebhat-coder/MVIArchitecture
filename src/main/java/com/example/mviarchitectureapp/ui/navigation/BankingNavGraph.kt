package com.example.mviarchitectureapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mviarchitectureapp.ui.home.HomeRoute
import com.example.mviarchitectureapp.ui.transactions.TransactionsRoute
import com.example.mviarchitectureapp.ui.transfer.TransferRoute
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"
    const val TRANSFER = "transfer"
    const val TRANSACTIONS = "transactions"
}

@Composable
fun BankingNavGraph() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(Routes.HOME) {
                HomeRoute(
                    onNavigateToTransfer = { navController.navigate(Routes.TRANSFER) },
                    onNavigateToTransactions = { navController.navigate(Routes.TRANSACTIONS) },
                )
            }
            composable(Routes.TRANSFER) {
                TransferRoute(
                    onBack = { navController.popBackStack() },
                    onTransferSuccess = { message ->
                        scope.launch { snackbarHostState.showSnackbar(message) }
                    },
                )
            }
            composable(Routes.TRANSACTIONS) {
                TransactionsRoute(onBack = { navController.popBackStack() })
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
