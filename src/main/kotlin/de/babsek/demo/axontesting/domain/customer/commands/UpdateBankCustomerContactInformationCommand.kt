package de.babsek.demo.axontesting.domain.customer.commands

import de.babsek.demo.axontesting.domain.customer.value.ContactInformation
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Command to update the persisted contact information for an existing bank customer.
 */
data class UpdateBankCustomerContactInformationCommand(
    @TargetAggregateIdentifier
    val bankCustomerId: String,
    val contactInformation: ContactInformation,
)
