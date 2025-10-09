package de.babsek.demo.axontesting.domain.commands

data class AcceptMoneyTransferCommand(
    override val bankAccountId: String,
    val amount: Double,
    val reason: String,
) : BankAccountCommand
