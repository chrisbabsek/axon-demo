package de.babsek.demo.axontesting.domain.customer.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Command indicating the successful verification of a bank customer's identity.
 */
data class VerifyBankCustomerIdentityCommand(
    @TargetAggregateIdentifier
    val bankCustomerId: String,
    val verificationReference: String,
)
