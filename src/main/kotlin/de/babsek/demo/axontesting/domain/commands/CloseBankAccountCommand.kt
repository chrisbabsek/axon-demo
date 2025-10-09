package de.babsek.demo.axontesting.domain.commands

import com.opencqrs.framework.command.Command

data class CloseBankAccountCommand(
    val bankAccountId: String,
) : Command {
    override fun getSubject(): String = "/bank-accounts/$bankAccountId"

    override fun getSubjectCondition(): Command.SubjectCondition = Command.SubjectCondition.EXISTS
}
