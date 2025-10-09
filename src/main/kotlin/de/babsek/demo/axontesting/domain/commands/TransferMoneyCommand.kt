package de.babsek.demo.axontesting.domain.commands

data class TransferMoneyCommand(
    override val bankAccountId: String,
    val destinationBankAccount: String,
    val amount: Double,
    val reason: String,
) : BankAccountCommand
