package com.example.mviarchitectureapp.ui.transactions

import com.example.mviarchitectureapp.data.model.Transaction
import com.example.mviarchitectureapp.mvi.MviEffect
import com.example.mviarchitectureapp.mvi.MviIntent
import com.example.mviarchitectureapp.mvi.MviState

sealed interface TransactionsIntent : MviIntent {
    data object Load : TransactionsIntent
}

data class TransactionsState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
) : MviState

sealed interface TransactionsEffect : MviEffect
