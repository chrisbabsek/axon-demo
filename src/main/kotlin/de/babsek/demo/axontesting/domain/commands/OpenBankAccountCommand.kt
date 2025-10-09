package de.babsek.demo.axontesting.domain.commands

import com.opencqrs.framework.command.Command

data class OpenBankAccountCommand(
    override val bankAccountId: String,
    val ownerName: String,
) : BankAccountCommand {
    override fun getSubjectCondition(): Command.SubjectCondition = Command.SubjectCondition.PRISTINE
}
