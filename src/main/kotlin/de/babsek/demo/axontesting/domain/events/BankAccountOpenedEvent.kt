package de.babsek.demo.axontesting.domain.events

import java.math.BigDecimal

data class BankAccountOpenedEvent(
    val bankAccountId: String,
    val ownerName: String,
    val initialBalance: BigDecimal,
)
