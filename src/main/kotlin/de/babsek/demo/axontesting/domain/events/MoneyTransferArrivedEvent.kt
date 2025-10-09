package de.babsek.demo.axontesting.domain.events

import java.math.BigDecimal

data class MoneyTransferArrivedEvent(
    val bankAccountId: String,
    val amount: BigDecimal,
    val reason: String,
)
