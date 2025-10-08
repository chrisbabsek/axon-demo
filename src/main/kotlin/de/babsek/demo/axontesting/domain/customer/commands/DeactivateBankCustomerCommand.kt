package de.babsek.demo.axontesting.domain.customer.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Command to deactivate an existing bank customer.
 */
data class DeactivateBankCustomerCommand(
    @TargetAggregateIdentifier
    val bankCustomerId: String,
    val reason: String,
)
