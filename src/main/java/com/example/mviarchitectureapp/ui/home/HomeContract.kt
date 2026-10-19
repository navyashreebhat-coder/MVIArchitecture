package com.example.mviarchitectureapp.ui.home

import com.example.mviarchitectureapp.data.model.Account
import com.example.mviarchitectureapp.data.model.AccountType
import com.example.mviarchitectureapp.data.model.Transaction
import com.example.mviarchitectureapp.data.model.UserProfile
import com.example.mviarchitectureapp.mvi.MviEffect
import com.example.mviarchitectureapp.mvi.MviIntent
import com.example.mviarchitectureapp.mvi.MviState

sealed interface HomeIntent : MviIntent {
    data object LoadDashboard : HomeIntent
    data object Refresh : HomeIntent
    data object OpenTransfer : HomeIntent
    data object OpenTransactions : HomeIntent
    data class SelectAccount(val accountId: String) : HomeIntent
}

data class HomeState(
    val isLoading: Boolean = true,
    val user: UserProfile? = null,
    val accounts: List<Account> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val selectedAccountId: String? = null,
    val errorMessage: String? = null,
) : MviState {
    val totalBalance: Double
        get() = accounts
            .filter { it.type != AccountType.CREDIT }
            .sumOf { it.balance }

    val selectedAccount: Account?
        get() = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
}

sealed interface HomeEffect : MviEffect {
    data object NavigateToTransfer : HomeEffect
    data object NavigateToTransactions : HomeEffect
}
