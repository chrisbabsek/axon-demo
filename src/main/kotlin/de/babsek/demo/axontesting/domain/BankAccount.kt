package de.babsek.demo.axontesting.domain

data class BankAccount(
    val bankAccountId: String,
    val ownerName: String,
    val balance: Double,
    val isClosed: Boolean,
)
