package de.babsek.demo.axontesting.domain

import java.math.BigDecimal

data class BankAccount(
    val bankAccountId: String,
    val ownerName: String,
    val balance: BigDecimal,
    val closed: Boolean,
) {
    fun deposit(amount: BigDecimal): BankAccount = copy(balance = balance + amount)

    fun withdraw(amount: BigDecimal): BankAccount = copy(balance = balance - amount)

    fun close(): BankAccount = copy(closed = true)

    companion object {
        fun new(bankAccountId: String, ownerName: String): BankAccount = BankAccount(
            bankAccountId = bankAccountId,
            ownerName = ownerName,
            balance = BigDecimal.ZERO,
            closed = false,
        )
    }
}
