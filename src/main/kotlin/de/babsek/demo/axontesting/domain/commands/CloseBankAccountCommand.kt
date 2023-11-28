package de.babsek.demo.axontesting.domain.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CloseBankAccountCommand(
    @TargetAggregateIdentifier
    val bankAccountId: String,
)
