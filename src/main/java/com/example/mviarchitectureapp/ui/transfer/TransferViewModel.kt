package com.example.mviarchitectureapp.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mviarchitectureapp.data.model.AccountType
import com.example.mviarchitectureapp.data.repository.BankingRepository
import com.example.mviarchitectureapp.mvi.MviViewModel
import kotlinx.coroutines.launch

class TransferViewModel(
    private val repository: BankingRepository = BankingRepository.getInstance(),
) : MviViewModel<TransferIntent, TransferState, TransferEffect>(TransferState()) {

    init {
        processIntent(TransferIntent.Load)
        viewModelScope.launch {
            repository.accounts.collect { accounts ->
                val transferable = accounts.filter { it.type != AccountType.CREDIT }
                reduce {
                    copy(
                        accounts = transferable,
                        fromAccountId = fromAccountId ?: transferable.firstOrNull()?.id,
                    )
                }
            }
        }
    }

    override suspend fun handleIntent(intent: TransferIntent) {
        when (intent) {
            TransferIntent.Load -> {
                reduce {
                    copy(
                        isLoading = false,
                        beneficiaries = repository.beneficiaries,
                        beneficiaryId = beneficiaryId ?: repository.beneficiaries.firstOrNull()?.id,
                    )
                }
            }

            is TransferIntent.SelectFromAccount -> reduce { copy(fromAccountId = intent.accountId) }
            is TransferIntent.SelectBeneficiary -> reduce { copy(beneficiaryId = intent.beneficiaryId) }
            is TransferIntent.AmountChanged -> reduce { copy(amount = intent.amount, errorMessage = null) }
            is TransferIntent.NoteChanged -> reduce { copy(note = intent.note) }
            TransferIntent.ClearError -> reduce { copy(errorMessage = null) }
            TransferIntent.SubmitTransfer -> submitTransfer()
        }
    }

    private suspend fun submitTransfer() {
        val amountValue = currentState.amount.toDoubleOrNull()
        val fromId = currentState.fromAccountId
        val toId = currentState.beneficiaryId

        if (amountValue == null || fromId == null || toId == null) {
            reduce { copy(errorMessage = "Please fill in all transfer details") }
            return
        }

        reduce { copy(isSubmitting = true, errorMessage = null) }

        repository.transfer(
            fromAccountId = fromId,
            beneficiaryId = toId,
            amount = amountValue,
            note = currentState.note,
        ).onSuccess {
            val beneficiaryName = currentState.selectedBeneficiary?.name ?: "recipient"
            reduce { copy(isSubmitting = false, amount = "", note = "") }
            sendEffect(
                TransferEffect.TransferCompleted(
                    "Transferred $${"%.2f".format(amountValue)} to $beneficiaryName",
                ),
            )
        }.onFailure { error ->
            reduce {
                copy(
                    isSubmitting = false,
                    errorMessage = error.message ?: "Transfer failed",
                )
            }
        }
    }

    class Factory(
        private val repository: BankingRepository = BankingRepository.getInstance(),
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransferViewModel(repository) as T
        }
    }
}
