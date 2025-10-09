package de.babsek.demo.axontesting.domain.commands

data class InformFailedMoneyTransferCommand(
    override val bankAccountId: String,
    val targetBankAccountId: String,
    val amount: Double,
    val errorMessage: String?,
) : BankAccountCommand
