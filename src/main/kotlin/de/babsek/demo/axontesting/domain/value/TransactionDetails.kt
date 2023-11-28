package de.babsek.demo.axontesting.domain.value

import java.time.Instant

data class TransactionDetails(
    val type: String,
    val date: Instant,
    val valuta: Double,
    val details: String,
)
