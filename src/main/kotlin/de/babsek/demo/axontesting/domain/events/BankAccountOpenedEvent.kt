package de.babsek.demo.axontesting.domain.events

/**
 * BankAccountOpenedEvent represents an event that is emitted when a bank account is opened.
 *
 * @property bankAccountId The unique identifier of the bank account.
 * @property ownerName This property holds the name of the individual or entity that owns the bank account.
 * @property initialBalance The initial balance of the bank account.
 */
data class BankAccountOpenedEvent(
    val bankAccountId: String,
    val ownerName: String,
    val initialBalance: Double,
)
