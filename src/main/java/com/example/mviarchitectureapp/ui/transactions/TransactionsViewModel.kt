package com.example.mviarchitectureapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mviarchitectureapp.data.repository.BankingRepository
import com.example.mviarchitectureapp.mvi.MviViewModel
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val repository: BankingRepository = BankingRepository.getInstance(),
) : MviViewModel<TransactionsIntent, TransactionsState, TransactionsEffect>(TransactionsState()) {

    init {
        viewModelScope.launch {
            repository.transactions.collect { list ->
                reduce { copy(isLoading = false, transactions = list) }
            }
        }
        processIntent(TransactionsIntent.Load)
    }

    override suspend fun handleIntent(intent: TransactionsIntent) {
        when (intent) {
            TransactionsIntent.Load -> reduce { copy(isLoading = false) }
        }
    }

    class Factory(
        private val repository: BankingRepository = BankingRepository.getInstance(),
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionsViewModel(repository) as T
        }
    }
}
