package de.babsek.demo.axontesting.domain.commands

import com.opencqrs.framework.command.Command

interface BankAccountCommand : Command {
    val bankAccountId: String

    override fun getSubject(): String = "/bank-account/$bankAccountId"

    override fun getSubjectCondition(): Command.SubjectCondition = Command.SubjectCondition.EXISTS
}
