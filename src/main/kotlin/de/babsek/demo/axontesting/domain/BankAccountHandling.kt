package de.babsek.demo.axontesting.domain

import com.opencqrs.framework.command.*
import de.babsek.demo.axontesting.domain.commands.*
import de.babsek.demo.axontesting.domain.events.*
import de.babsek.demo.axontesting.domain.exceptions.BankAccountAlreadyClosedException
import de.babsek.demo.axontesting.domain.exceptions.BankAccountMustBeBalancedForCloseException
import de.babsek.demo.axontesting.domain.exceptions.NotEnoughMoneyException

@CommandHandlerConfiguration
class BankAccountHandling {

    @CommandHandling(sourcingMode = SourcingMode.LOCAL)
    fun openBankAccount(
        command: OpenBankAccountCommand,
        eventPublisher: CommandEventPublisher<BankAccount>,
    ): String {
        eventPublisher.publish(
            BankAccountOpenedEvent(
                bankAccountId = command.bankAccountId,
                ownerName = command.ownerName,
                initialBalance = 0.0,
            ),
        )
        return command.bankAccountId
    }

    @StateRebuilding
    fun on(event: BankAccountOpenedEvent): BankAccount = BankAccount(
        bankAccountId = event.bankAccountId,
        ownerName = event.ownerName,
        balance = event.initialBalance,
        isClosed = false,
    )

    @CommandHandling
    fun acceptMoneyTransfer(
        bankAccount: BankAccount,
        command: AcceptMoneyTransferCommand,
        eventPublisher: CommandEventPublisher<BankAccount>,
    ) {
        ensureOpen(bankAccount)
        eventPublisher.publish(
            MoneyTransferArrivedEvent(
                bankAccountId = command.bankAccountId,
                amount = command.amount,
                reason = command.reason,
            ),
        )
    }

    @StateRebuilding
    fun on(bankAccount: BankAccount, event: MoneyTransferArrivedEvent): BankAccount =
        bankAccount.copy(balance = bankAccount.balance + event.amount)

    @CommandHandling
    fun transferMoney(
        bankAccount: BankAccount,
        command: TransferMoneyCommand,
        eventPublisher: CommandEventPublisher<BankAccount>,
    ) {
        ensureOpen(bankAccount)
        if (bankAccount.balance < command.amount) {
            throw NotEnoughMoneyException(
                bankAccountId = command.bankAccountId,
                requestedAmount = command.amount,
                currentBalance = bankAccount.balance,
            )
        }

        eventPublisher.publish(
            MoneyTransferRequestedEvent(
                originBankAccountId = command.bankAccountId,
                targetBankAccountId = command.destinationBankAccount,
                amount = command.amount,
                reason = command.reason,
            ),
        )
    }

    @StateRebuilding
    fun on(bankAccount: BankAccount, event: MoneyTransferRequestedEvent): BankAccount =
        bankAccount.copy(balance = bankAccount.balance - event.amount)

    @CommandHandling
    fun informFailedMoneyTransfer(
        bankAccount: BankAccount,
        command: InformFailedMoneyTransferCommand,
        eventPublisher: CommandEventPublisher<BankAccount>,
    ) {
        ensureOpen(bankAccount)
        eventPublisher.publish(
            MoneyTransferFailedEvent(
                bankAccountId = command.bankAccountId,
                targetBankAccountId = command.targetBankAccountId,
                amount = command.amount,
                errorMessage = command.errorMessage,
            ),
        )
    }

    @StateRebuilding
    fun on(bankAccount: BankAccount, event: MoneyTransferFailedEvent): BankAccount =
        bankAccount.copy(balance = bankAccount.balance + event.amount)

    @CommandHandling
    fun closeBankAccount(
        bankAccount: BankAccount,
        command: CloseBankAccountCommand,
        eventPublisher: CommandEventPublisher<BankAccount>,
    ) {
        ensureOpen(bankAccount)
        if (bankAccount.balance != 0.0) {
            throw BankAccountMustBeBalancedForCloseException(
                bankAccountId = command.bankAccountId,
                remainingBalance = bankAccount.balance,
            )
        }
        eventPublisher.publish(
            BankAccountClosedEvent(
                bankAccountId = command.bankAccountId,
            ),
        )
    }

    @StateRebuilding
    fun on(bankAccount: BankAccount, event: BankAccountClosedEvent): BankAccount =
        bankAccount.copy(isClosed = true)

    private fun ensureOpen(bankAccount: BankAccount) {
        if (bankAccount.isClosed) {
            throw BankAccountAlreadyClosedException(bankAccount.bankAccountId)
        }
    }
}
