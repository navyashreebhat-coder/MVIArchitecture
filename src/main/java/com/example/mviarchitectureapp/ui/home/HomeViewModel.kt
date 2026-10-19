package com.example.mviarchitectureapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mviarchitectureapp.data.repository.BankingRepository
import com.example.mviarchitectureapp.mvi.MviViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: BankingRepository = BankingRepository.getInstance(),
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    init {
        observeRepository()
        processIntent(HomeIntent.LoadDashboard)
    }

    private fun observeRepository() {
        viewModelScope.launch {
            combine(repository.accounts, repository.transactions) { accounts, transactions ->
                accounts to transactions
            }.collect { (accounts, transactions) ->
                reduce {
                    copy(
                        accounts = accounts,
                        recentTransactions = transactions.take(5),
                        selectedAccountId = selectedAccountId ?: accounts.firstOrNull()?.id,
                        user = repository.userProfile,
                    )
                }
            }
        }
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadDashboard, HomeIntent.Refresh -> loadDashboard()
            HomeIntent.OpenTransfer -> sendEffect(HomeEffect.NavigateToTransfer)
            HomeIntent.OpenTransactions -> sendEffect(HomeEffect.NavigateToTransactions)
            is HomeIntent.SelectAccount -> reduce { copy(selectedAccountId = intent.accountId) }
        }
    }

    private suspend fun loadDashboard() {
        reduce { copy(isLoading = true, errorMessage = null) }
        runCatching {
            repository.loadDashboard()
        }.onSuccess {
            reduce { copy(isLoading = false) }
        }.onFailure { error ->
            reduce {
                copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Failed to load dashboard",
                )
            }
        }
    }

    class Factory(
        private val repository: BankingRepository = BankingRepository.getInstance(),
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
