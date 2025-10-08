package de.babsek.demo.axontesting.domain.customer.events

/**
 * Event emitted once a bank customer's identity has been verified.
 */
data class BankCustomerIdentityVerifiedEvent(
    val bankCustomerId: String,
    val verificationReference: String,
)
