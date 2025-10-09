package de.babsek.demo.axontesting.domain.events

import java.math.BigDecimal

data class MoneyTransferRequestedEvent(
    val originBankAccountId: String,
    val targetBankAccountId: String,
    val amount: BigDecimal,
    val reason: String,
)
