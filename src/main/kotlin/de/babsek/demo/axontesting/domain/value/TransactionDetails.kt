package de.babsek.demo.axontesting.domain.value

import java.math.BigDecimal
import java.time.Instant

data class TransactionDetails(
    val type: String,
    val date: Instant,
    val valuta: BigDecimal,
    val details: String,
)
