package de.babsek.demo.axontesting.domain.commands

data class CloseBankAccountCommand(
    override val bankAccountId: String,
) : BankAccountCommand
