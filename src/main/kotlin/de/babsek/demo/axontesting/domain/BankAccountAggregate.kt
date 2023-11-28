package de.babsek.demo.axontesting.domain

import de.babsek.demo.axontesting.domain.commands.*
import de.babsek.demo.axontesting.domain.events.*
import de.babsek.demo.axontesting.domain.exceptions.BankAccountAlreadyExistingException
import de.babsek.demo.axontesting.domain.exceptions.BankAccountMustBeBalancedForCloseException
import de.babsek.demo.axontesting.domain.exceptions.NotEnoughMoneyException
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class BankAccountAggregate {
    @AggregateIdentifier
    lateinit var bankAccountId: String
    lateinit var ownerName: String

    var balance: Double = 0.0

    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    @CommandHandler
    fun openBankAccount(command: OpenBankAccountCommand): String {
        if (this::bankAccountId.isInitialized) {
            throw BankAccountAlreadyExistingException(bankAccountId = command.bankAccountId)
        }

        AggregateLifecycle.apply(
            BankAccountOpenedEvent(
                bankAccountId = command.bankAccountId,
                ownerName = command.ownerName,
                initialBalance = 0.0,
            ),
        )
        return command.bankAccountId
    }

    @EventSourcingHandler
    fun on(event: BankAccountOpenedEvent) {
        bankAccountId = event.bankAccountId
        ownerName = event.ownerName
        balance = event.initialBalance
    }

    @CommandHandler
    fun acceptMoneyTransfer(command: AcceptMoneyTransferCommand) {
        AggregateLifecycle.apply(
            MoneyTransferArrivedEvent(
                bankAccountId = command.bankAccountId,
                amount = command.amount,
                reason = command.reason,
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferArrivedEvent) {
        balance += event.amount
    }

    @CommandHandler
    fun transferMoney(command: TransferMoneyCommand) {
        if (balance < command.amount) {
            throw NotEnoughMoneyException(
                bankAccountId = command.bankAccountId,
                requestedAmount = command.amount,
                currentBalance = balance,
            )
        } else {
            AggregateLifecycle.apply(
                MoneyTransferRequestedEvent(
                    originBankAccountId = command.bankAccountId,
                    targetBankAccountId = command.destinationBankAccount,
                    amount = command.amount,
                    reason = command.reason,
                ),
            )
        }
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferRequestedEvent) {
        balance -= event.amount
    }

    @CommandHandler
    fun handleFailedMoneyTransfer(command: InformFailedMoneyTransferCommand) {
        AggregateLifecycle.apply(
            MoneyTransferFailedEvent(
                bankAccountId = command.bankAccountId,
                targetBankAccountId = command.targetBankAccountId,
                amount = command.amount,
                errorMessage = command.errorMessage,
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferFailedEvent) {
        balance += event.amount
    }

    @CommandHandler
    fun handleCloseBankAccount(command: CloseBankAccountCommand) {
        if (balance != 0.0) {
            throw BankAccountMustBeBalancedForCloseException(
                bankAccountId = command.bankAccountId,
                remainingBalance = balance,
            )
        }
        AggregateLifecycle.apply(
            BankAccountClosedEvent(
                bankAccountId = command.bankAccountId,
            )
        )
    }

    @EventSourcingHandler
    fun on(event: BankAccountClosedEvent) {
        AggregateLifecycle.markDeleted()
    }
}
