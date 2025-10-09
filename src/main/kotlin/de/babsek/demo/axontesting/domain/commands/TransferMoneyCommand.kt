package de.babsek.demo.axontesting.domain.commands

import com.opencqrs.framework.command.Command
import java.math.BigDecimal

data class TransferMoneyCommand(
    val bankAccountId: String,
    val destinationBankAccount: String,
    val amount: BigDecimal,
    val reason: String,
) : Command {
    override fun getSubject(): String = "/bank-accounts/$bankAccountId"

    override fun getSubjectCondition(): Command.SubjectCondition = Command.SubjectCondition.EXISTS
}
