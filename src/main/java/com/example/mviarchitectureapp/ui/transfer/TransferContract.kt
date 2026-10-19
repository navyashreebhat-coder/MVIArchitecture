package com.example.mviarchitectureapp.ui.transfer

import com.example.mviarchitectureapp.data.model.Account
import com.example.mviarchitectureapp.data.model.Beneficiary
import com.example.mviarchitectureapp.mvi.MviEffect
import com.example.mviarchitectureapp.mvi.MviIntent
import com.example.mviarchitectureapp.mvi.MviState

sealed interface TransferIntent : MviIntent {
    data object Load : TransferIntent
    data class SelectFromAccount(val accountId: String) : TransferIntent
    data class SelectBeneficiary(val beneficiaryId: String) : TransferIntent
    data class AmountChanged(val amount: String) : TransferIntent
    data class NoteChanged(val note: String) : TransferIntent
    data object SubmitTransfer : TransferIntent
    data object ClearError : TransferIntent
}

data class TransferState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val beneficiaries: List<Beneficiary> = emptyList(),
    val fromAccountId: String? = null,
    val beneficiaryId: String? = null,
    val amount: String = "",
    val note: String = "",
    val errorMessage: String? = null,
) : MviState {
    val fromAccount: Account?
        get() = accounts.find { it.id == fromAccountId }

    val selectedBeneficiary: Beneficiary?
        get() = beneficiaries.find { it.id == beneficiaryId }

    val canSubmit: Boolean
        get() = !isSubmitting &&
            fromAccountId != null &&
            beneficiaryId != null &&
            (amount.toDoubleOrNull() ?: 0.0) > 0
}

sealed interface TransferEffect : MviEffect {
    data class TransferCompleted(val message: String) : TransferEffect
}
