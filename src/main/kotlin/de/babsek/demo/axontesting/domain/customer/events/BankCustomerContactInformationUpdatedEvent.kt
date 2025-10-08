package de.babsek.demo.axontesting.domain.customer.events

import de.babsek.demo.axontesting.domain.customer.value.ContactInformation

/**
 * Event emitted whenever the contact information for a bank customer changes.
 */
data class BankCustomerContactInformationUpdatedEvent(
    val bankCustomerId: String,
    val contactInformation: ContactInformation,
)
