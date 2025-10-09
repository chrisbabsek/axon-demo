package de.babsek.demo.axontesting.domain

import com.opencqrs.framework.command.CommandEventPublisher
import com.opencqrs.framework.command.CommandHandlerConfiguration
import com.opencqrs.framework.command.CommandHandling
import com.opencqrs.framework.command.StateRebuilding
import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.CloseBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.InformFailedMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.OpenBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.TransferMoneyCommand
import de.babsek.demo.axontesting.domain.events.BankAccountClosedEvent
import de.babsek.demo.axontesting.domain.events.BankAccountOpenedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferArrivedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferFailedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import de.babsek.demo.axontesting.domain.exceptions.BankAccountAlreadyClosedException
import de.babsek.demo.axontesting.domain.exceptions.BankAccountMustBeBalancedForCloseException
import de.babsek.demo.axontesting.domain.exceptions.NotEnoughMoneyException
import java.math.BigDecimal

@CommandHandlerConfiguration
class BankAccountHandling {

    @CommandHandling
    fun handle(command: OpenBankAccountCommand, publisher: CommandEventPublisher<BankAccount>): String {
        publisher.publish(
            BankAccountOpenedEvent(
                bankAccountId = command.bankAccountId,
                ownerName = command.ownerName,
                initialBalance = BigDecimal.ZERO,
            ),
        )

        return command.bankAccountId
    }

    @StateRebuilding
    fun on(event: BankAccountOpenedEvent): BankAccount =
        BankAccount.new(event.bankAccountId, event.ownerName)
            .copy(balance = event.initialBalance)

    @CommandHandling
    fun handle(
        account: BankAccount,
        command: AcceptMoneyTransferCommand,
        publisher: CommandEventPublisher<BankAccount>,
    ) {
        ensureOpen(account)

        publisher.publish(
            MoneyTransferArrivedEvent(
                bankAccountId = account.bankAccountId,
                amount = command.amount,
                reason = command.reason,
            ),
        )
    }

    @StateRebuilding
    fun on(account: BankAccount, event: MoneyTransferArrivedEvent): BankAccount =
        account.deposit(event.amount)

    @CommandHandling
    fun handle(
        account: BankAccount,
        command: TransferMoneyCommand,
        publisher: CommandEventPublisher<BankAccount>,
    ) {
        ensureOpen(account)

        if (account.balance < command.amount) {
            throw NotEnoughMoneyException(
                bankAccountId = account.bankAccountId,
                requestedAmount = command.amount,
                currentBalance = account.balance,
            )
        }

        publisher.publish(
            MoneyTransferRequestedEvent(
                originBankAccountId = account.bankAccountId,
                targetBankAccountId = command.destinationBankAccount,
                amount = command.amount,
                reason = command.reason,
            ),
        )
    }

    @StateRebuilding
    fun on(account: BankAccount, event: MoneyTransferRequestedEvent): BankAccount =
        account.withdraw(event.amount)

    @CommandHandling
    fun handle(
        account: BankAccount,
        command: InformFailedMoneyTransferCommand,
        publisher: CommandEventPublisher<BankAccount>,
    ) {
        publisher.publish(
            MoneyTransferFailedEvent(
                bankAccountId = account.bankAccountId,
                targetBankAccountId = command.targetBankAccountId,
                amount = command.amount,
                errorMessage = command.errorMessage,
            ),
        )
    }

    @StateRebuilding
    fun on(account: BankAccount, event: MoneyTransferFailedEvent): BankAccount =
        account.deposit(event.amount)

    @CommandHandling
    fun handle(
        account: BankAccount,
        command: CloseBankAccountCommand,
        publisher: CommandEventPublisher<BankAccount>,
    ) {
        if (account.closed) {
            throw BankAccountAlreadyClosedException(account.bankAccountId)
        }

        if (account.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw BankAccountMustBeBalancedForCloseException(
                bankAccountId = account.bankAccountId,
                remainingBalance = account.balance,
            )
        }

        publisher.publish(
            BankAccountClosedEvent(
                bankAccountId = account.bankAccountId,
            ),
        )
    }

    @StateRebuilding
    fun on(account: BankAccount, event: BankAccountClosedEvent): BankAccount =
        account.close()

    private fun ensureOpen(account: BankAccount) {
        if (account.closed) {
            throw BankAccountAlreadyClosedException(account.bankAccountId)
        }
    }
}
