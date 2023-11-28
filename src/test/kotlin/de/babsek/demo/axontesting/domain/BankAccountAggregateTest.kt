package de.babsek.demo.axontesting.domain

import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.OpenBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.TransferMoneyCommand
import de.babsek.demo.axontesting.domain.events.BankAccountOpenedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferArrivedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import de.babsek.demo.axontesting.domain.exceptions.BankAccountAlreadyExistingException
import de.babsek.demo.axontesting.domain.exceptions.NotEnoughMoneyException
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class BankAccountAggregateTest {
    val fixture by lazy { AggregateTestFixture(BankAccountAggregate::class.java) }

    @Nested
    inner class OpenBankAccount {
        @Test
        fun `can open new bank account`() {
            fixture
                .givenNoPriorActivity()
                .`when`(
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
                .`when`(
                    OpenBankAccountCommand(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                    )
                )
                .expectException(BankAccountAlreadyExistingException::class.java)
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
                .`when`(
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
                .`when`(
                    TransferMoneyCommand(
                        bankAccountId = "001",
                        destinationBankAccount = "002",
                        amount = 1001.0,
                        reason = "rent payment 11/23"
                    )
                )
                .expectException(NotEnoughMoneyException::class.java)
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
                .`when`(
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

}
