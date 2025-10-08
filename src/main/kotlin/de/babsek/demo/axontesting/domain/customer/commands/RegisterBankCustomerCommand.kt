package de.babsek.demo.axontesting.domain.customer.commands

import de.babsek.demo.axontesting.domain.customer.value.ContactInformation
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Command to register a new bank customer in the system.
 */
data class RegisterBankCustomerCommand(
    @TargetAggregateIdentifier
    val bankCustomerId: String,
    val fullName: String,
    val contactInformation: ContactInformation,
)
