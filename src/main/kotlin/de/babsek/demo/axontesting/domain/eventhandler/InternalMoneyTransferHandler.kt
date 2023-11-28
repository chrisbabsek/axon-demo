package de.babsek.demo.axontesting.domain.eventhandler

import de.babsek.demo.axontesting.configuration.ProcessingGroups
import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.InformFailedMoneyTransferCommand
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@ProcessingGroup(ProcessingGroups.INTERNAL_MONEY_TRANSFER)
@Component
class InternalMoneyTransferHandler(
    private val commandGateway: CommandGateway,
) {

    @EventHandler
    fun on(event: MoneyTransferRequestedEvent) {
        try {
            commandGateway.sendAndWait<Unit>(
                AcceptMoneyTransferCommand(
                    bankAccountId = event.targetBankAccountId,
                    amount = event.amount,
                    reason = event.reason,
                ),
            )
        } catch (e: Exception) {
            commandGateway.sendAndWait<Unit>(
                InformFailedMoneyTransferCommand(
                    bankAccountId = event.originBankAccountId,
                    targetBankAccountId = event.targetBankAccountId,
                    amount = event.amount,
                    errorMessage = e.message,
                ),
            )
        }
    }
}
