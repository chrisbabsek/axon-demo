package de.babsek.demo.axontesting.domain.eventhandler

import com.opencqrs.framework.command.CommandRouter
import com.opencqrs.framework.eventhandler.EventHandling
import de.babsek.demo.axontesting.configuration.ProcessingGroups
import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.InformFailedMoneyTransferCommand
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import org.springframework.stereotype.Component

@Component
class InternalMoneyTransferHandler(
    private val commandRouter: CommandRouter,
) {

    @EventHandling(ProcessingGroups.INTERNAL_MONEY_TRANSFER)
    fun on(event: MoneyTransferRequestedEvent) {
        runCatching {
            commandRouter.send<Unit>(
                AcceptMoneyTransferCommand(
                    bankAccountId = event.targetBankAccountId,
                    amount = event.amount,
                    reason = event.reason,
                ),
            )
        }.onFailure { throwable ->
            commandRouter.send<Unit>(
                InformFailedMoneyTransferCommand(
                    bankAccountId = event.originBankAccountId,
                    targetBankAccountId = event.targetBankAccountId,
                    amount = event.amount,
                    errorMessage = throwable.message,
                ),
            )
        }
    }
}
