package de.babsek.demo.axontesting.domain.customer.events

import de.babsek.demo.axontesting.domain.customer.value.ContactInformation

/**
 * Event emitted once a new bank customer has been successfully registered.
 */
data class BankCustomerRegisteredEvent(
    val bankCustomerId: String,
    val fullName: String,
    val contactInformation: ContactInformation,
)
