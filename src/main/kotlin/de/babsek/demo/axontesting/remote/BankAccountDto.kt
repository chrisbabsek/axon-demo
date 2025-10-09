package de.babsek.demo.axontesting.remote

import de.babsek.demo.axontesting.domain.value.TransactionDetails
import java.math.BigDecimal

data class BankAccountDto(
    val bankAccountId: String,
    val ownerName: String,
    val balance: BigDecimal,
    val transactions: List<TransactionDetails>,
)
