package com.example.mviarchitectureapp.data.model

data class UserProfile(
    val name: String,
    val email: String,
    val memberSince: String,
)

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val accountNumberMasked: String,
    val balance: Double,
    val currency: String = "USD",
)

enum class AccountType {
    CHECKING,
    SAVINGS,
    CREDIT,
}

data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val date: String,
    val type: TransactionType,
    val accountId: String,
)

enum class TransactionType {
    CREDIT,
    DEBIT,
}

data class Beneficiary(
    val id: String,
    val name: String,
    val accountNumberMasked: String,
    val bankName: String,
)
