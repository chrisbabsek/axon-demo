package de.babsek.demo.axontesting.domain

import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.CloseBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.OpenBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.TransferMoneyCommand
import de.babsek.demo.axontesting.domain.events.BankAccountClosedEvent
import de.babsek.demo.axontesting.domain.events.BankAccountOpenedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferArrivedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import de.babsek.demo.axontesting.domain.exceptions.BankAccountAlreadyExistingException
import de.babsek.demo.axontesting.domain.exceptions.BankAccountMustBeBalancedForCloseException
import de.babsek.demo.axontesting.domain.exceptions.NotEnoughMoneyException
import org.axonframework.eventsourcing.AggregateDeletedException
import org.axonframework.extension.kotlin.test.aggregateTestFixture
import org.axonframework.extension.kotlin.test.expectException
import org.axonframework.extension.kotlin.test.whenever
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class BankAccountAggregateTest {
    val fixture = aggregateTestFixture<BankAccountAggregate>()

    @Nested
    inner class OpenBankAccount {
        @Test
        fun `can open new bank account`() {
            fixture
                .givenNoPriorActivity()
                .whenever(
                    OpenBankAccountCommand(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 0.0,
                    )
                )
        }

        @Test
        fun `deny to open new bank account with existing id`() {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 0.0,
                    )
                )
                .whenever(
                    OpenBankAccountCommand(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                    )
                )
                .expectException(BankAccountAlreadyExistingException::class)
        }
    }

    @Nested
    inner class AcceptMoneyTransfer {
        @Test
        fun `can accept money transfer`() {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 0.0,
                    )
                )
                .whenever(
                    AcceptMoneyTransferCommand(
                        bankAccountId = "001",
                        amount = 2500.0,
                        reason = "salary 11/23",
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    MoneyTransferArrivedEvent(
                        bankAccountId = "001",
                        amount = 2500.0,
                        reason = "salary 11/23",
                    )
                )
        }
    }

    @Nested
    inner class TransferMoney {
        @Test
        fun `fail to transfer money if not enough money on bank account`() {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 1000.0,
                    )
                )
                .whenever(
                    TransferMoneyCommand(
                        bankAccountId = "001",
                        destinationBankAccount = "002",
                        amount = 1001.0,
                        reason = "rent payment 11/23"
                    )
                )
                .expectException(NotEnoughMoneyException::class)
        }

        @Test
        fun `transfer money on enough money available`() {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 1000.0,
                    )
                )
                .whenever(
                    TransferMoneyCommand(
                        bankAccountId = "001",
                        destinationBankAccount = "002",
                        amount = 850.0,
                        reason = "rent payment 11/23"
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    MoneyTransferRequestedEvent(
                        originBankAccountId = "001",
                        targetBankAccountId = "002",
                        amount = 850.0,
                        reason = "rent payment 11/23"
                    )
                )
        }
    }

    @Nested
    inner class CloseBankAccount {
        @Test
        fun `can close balanced non-closed bank account`() {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 0.0,
                    )
                )
                .whenever(
                    CloseBankAccountCommand(
                        bankAccountId = "001",
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    BankAccountClosedEvent(
                        bankAccountId = "001",
                    )
                )
                .expectMarkedDeleted()
        }

        @Test
        fun `fail to close non-balanced bank account`() {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 100.0,
                    )
                )
                .whenever(
                    CloseBankAccountCommand(
                        bankAccountId = "001",
                    )
                )
                .expectException(BankAccountMustBeBalancedForCloseException::class)
        }

        @Test
        fun `fail to close already closed bank account`() {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 0.0,
                    )
                )
                .andGiven(
                    BankAccountClosedEvent(
                        bankAccountId = "001",
                    )
                )
                .whenever(
                    CloseBankAccountCommand(
                        bankAccountId = "001",
                    )
                )
                .expectException(AggregateDeletedException::class)
        }
    }

}
