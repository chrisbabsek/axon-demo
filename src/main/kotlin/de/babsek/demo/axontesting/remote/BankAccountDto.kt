package de.babsek.demo.axontesting.remote

import de.babsek.demo.axontesting.domain.value.TransactionDetails

data class BankAccountDto(
    val bankAccountId: String,
    val ownerName: String,
    val balance: Double,
    val transactions: List<TransactionDetails>,
)
