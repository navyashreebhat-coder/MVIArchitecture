package com.example.mviarchitectureapp.data.repository

import com.example.mviarchitectureapp.data.model.Account
import com.example.mviarchitectureapp.data.model.AccountType
import com.example.mviarchitectureapp.data.model.Beneficiary
import com.example.mviarchitectureapp.data.model.Transaction
import com.example.mviarchitectureapp.data.model.TransactionType
import com.example.mviarchitectureapp.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * In-memory banking data with static seed values.
 * Transfer updates balances and prepends a fake transaction.
 */
class BankingRepository {

    private val _accounts = MutableStateFlow(seedAccounts())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _transactions = MutableStateFlow(seedTransactions())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    val userProfile: UserProfile = UserProfile(
        name = "Alex Morgan",
        email = "alex.morgan@email.com",
        memberSince = "March 2019",
    )

    val beneficiaries: List<Beneficiary> = listOf(
        Beneficiary("b1", "Jordan Lee", "•••• 4821", "First National"),
        Beneficiary("b2", "Sam Rivera", "•••• 9033", "Pacific Credit Union"),
        Beneficiary("b3", "Casey Kim", "•••• 1170", "Metro Bank"),
    )

    suspend fun loadDashboard() {
        delay(400)
    }

    suspend fun transfer(
        fromAccountId: String,
        beneficiaryId: String,
        amount: Double,
        note: String,
    ): Result<Unit> {
        delay(700)

        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }

        val fromAccount = _accounts.value.find { it.id == fromAccountId }
            ?: return Result.failure(IllegalArgumentException("Source account not found"))

        if (fromAccount.type == AccountType.CREDIT) {
            return Result.failure(IllegalArgumentException("Cannot transfer from a credit account"))
        }

        if (fromAccount.balance < amount) {
            return Result.failure(IllegalArgumentException("Insufficient funds"))
        }

        val beneficiary = beneficiaries.find { it.id == beneficiaryId }
            ?: return Result.failure(IllegalArgumentException("Beneficiary not found"))

        _accounts.update { list ->
            list.map { account ->
                if (account.id == fromAccountId) {
                    account.copy(balance = account.balance - amount)
                } else {
                    account
                }
            }
        }

        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            title = "Transfer to ${beneficiary.name}",
            subtitle = note.ifBlank { beneficiary.bankName },
            amount = -amount,
            date = "Just now",
            type = TransactionType.DEBIT,
            accountId = fromAccountId,
        )

        _transactions.update { listOf(transaction) + it }
        return Result.success(Unit)
    }

    private fun seedAccounts(): List<Account> = listOf(
        Account(
            id = "acc_checking",
            name = "Everyday Checking",
            type = AccountType.CHECKING,
            accountNumberMasked = "•••• 2048",
            balance = 4_852.40,
        ),
        Account(
            id = "acc_savings",
            name = "Rainy Day Savings",
            type = AccountType.SAVINGS,
            accountNumberMasked = "•••• 7712",
            balance = 12_340.00,
        ),
        Account(
            id = "acc_credit",
            name = "Rewards Credit Card",
            type = AccountType.CREDIT,
            accountNumberMasked = "•••• 5590",
            balance = -842.15,
        ),
    )

    private fun seedTransactions(): List<Transaction> = listOf(
        Transaction(
            id = "tx1",
            title = "Salary Deposit",
            subtitle = "Acme Corp Payroll",
            amount = 3_200.00,
            date = "Oct 17",
            type = TransactionType.CREDIT,
            accountId = "acc_checking",
        ),
        Transaction(
            id = "tx2",
            title = "Grocery Mart",
            subtitle = "Groceries",
            amount = -64.28,
            date = "Oct 16",
            type = TransactionType.DEBIT,
            accountId = "acc_checking",
        ),
        Transaction(
            id = "tx3",
            title = "Transfer to Savings",
            subtitle = "Rainy Day Savings",
            amount = -500.00,
            date = "Oct 15",
            type = TransactionType.DEBIT,
            accountId = "acc_checking",
        ),
        Transaction(
            id = "tx4",
            title = "Interest Credit",
            subtitle = "Monthly interest",
            amount = 12.45,
            date = "Oct 14",
            type = TransactionType.CREDIT,
            accountId = "acc_savings",
        ),
        Transaction(
            id = "tx5",
            title = "Coffee House",
            subtitle = "Food & drink",
            amount = -5.75,
            date = "Oct 14",
            type = TransactionType.DEBIT,
            accountId = "acc_checking",
        ),
        Transaction(
            id = "tx6",
            title = "Streaming Service",
            subtitle = "Entertainment",
            amount = -15.99,
            date = "Oct 12",
            type = TransactionType.DEBIT,
            accountId = "acc_credit",
        ),
        Transaction(
            id = "tx7",
            title = "Gas Station",
            subtitle = "Transport",
            amount = -42.10,
            date = "Oct 11",
            type = TransactionType.DEBIT,
            accountId = "acc_checking",
        ),
    )

    companion object {
        @Volatile
        private var instance: BankingRepository? = null

        fun getInstance(): BankingRepository {
            return instance ?: synchronized(this) {
                instance ?: BankingRepository().also { instance = it }
            }
        }
    }
}
