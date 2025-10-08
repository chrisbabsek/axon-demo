package de.babsek.demo.axontesting.domain.customer.events

/**
 * Event emitted when a bank customer is deactivated.
 */
data class BankCustomerDeactivatedEvent(
    val bankCustomerId: String,
    val reason: String,
)
