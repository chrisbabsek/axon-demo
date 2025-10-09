package de.babsek.demo.axontesting.domain.commands

import com.opencqrs.framework.command.Command
import java.math.BigDecimal

data class InformFailedMoneyTransferCommand(
    val bankAccountId: String,
    val targetBankAccountId: String,
    val amount: BigDecimal,
    val errorMessage: String?,
) : Command {
    override fun getSubject(): String = "/bank-accounts/$bankAccountId"

    override fun getSubjectCondition(): Command.SubjectCondition = Command.SubjectCondition.EXISTS
}
