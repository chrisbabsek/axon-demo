package de.babsek.demo.axontesting.domain.commands

import com.opencqrs.framework.command.Command

data class OpenBankAccountCommand(
    val bankAccountId: String,
    val ownerName: String,
) : Command {
    override fun getSubject(): String = "/bank-accounts/$bankAccountId"

    override fun getSubjectCondition(): Command.SubjectCondition = Command.SubjectCondition.PRISTINE
}
