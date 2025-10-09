package de.babsek.demo.axontesting.domain.events

import java.math.BigDecimal

data class MoneyTransferFailedEvent(
    val bankAccountId: String,
    val targetBankAccountId: String,
    val amount: BigDecimal,
    val errorMessage: String?,
)
