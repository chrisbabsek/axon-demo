package de.babsek.demo.axontesting.domain

import com.opencqrs.framework.command.CommandHandlingTest
import com.opencqrs.framework.command.CommandHandlingTestFixture
import com.opencqrs.framework.command.CommandSubjectAlreadyExistsException
import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.CloseBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.OpenBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.TransferMoneyCommand
import de.babsek.demo.axontesting.domain.events.BankAccountClosedEvent
import de.babsek.demo.axontesting.domain.events.BankAccountOpenedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferArrivedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import de.babsek.demo.axontesting.domain.exceptions.BankAccountAlreadyClosedException
import de.babsek.demo.axontesting.domain.exceptions.BankAccountMustBeBalancedForCloseException
import de.babsek.demo.axontesting.domain.exceptions.NotEnoughMoneyException
import java.math.BigDecimal
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@CommandHandlingTest
class BankAccountHandlingTest {

    @Nested
    inner class OpenBankAccount {
        @Test
        fun `can open new bank account`(
            @Autowired fixture: CommandHandlingTestFixture<OpenBankAccountCommand>,
        ) {
            fixture
                .givenNothing()
                .`when`(
                    OpenBankAccountCommand(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                    ),
                )
                .expectResult("001")
                .expectSingleEvent(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal.ZERO,
                    ),
                )
        }

        @Test
        fun `deny to open new bank account with existing id`(
            @Autowired fixture: CommandHandlingTestFixture<OpenBankAccountCommand>,
        ) {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal.ZERO,
                    ),
                )
                .`when`(
                    OpenBankAccountCommand(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                    ),
                )
                .expectException(CommandSubjectAlreadyExistsException::class.java)
        }
    }

    @Nested
    inner class AcceptMoneyTransfer {
        @Test
        fun `can accept money transfer`(
            @Autowired fixture: CommandHandlingTestFixture<AcceptMoneyTransferCommand>,
        ) {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal.ZERO,
                    ),
                )
                .`when`(
                    AcceptMoneyTransferCommand(
                        bankAccountId = "001",
                        amount = BigDecimal("2500.00"),
                        reason = "salary 11/23",
                    ),
                )
                .expectSuccessfulExecution()
                .expectSingleEvent(
                    MoneyTransferArrivedEvent(
                        bankAccountId = "001",
                        amount = BigDecimal("2500.00"),
                        reason = "salary 11/23",
                    ),
                )
        }
    }

    @Nested
    inner class TransferMoney {
        @Test
        fun `fail to transfer money if not enough money on bank account`(
            @Autowired fixture: CommandHandlingTestFixture<TransferMoneyCommand>,
        ) {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal("1000.00"),
                    ),
                )
                .`when`(
                    TransferMoneyCommand(
                        bankAccountId = "001",
                        destinationBankAccount = "002",
                        amount = BigDecimal("1001.00"),
                        reason = "rent payment 11/23",
                    ),
                )
                .expectException(NotEnoughMoneyException::class.java)
        }

        @Test
        fun `transfer money on enough money available`(
            @Autowired fixture: CommandHandlingTestFixture<TransferMoneyCommand>,
        ) {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal("1000.00"),
                    ),
                )
                .`when`(
                    TransferMoneyCommand(
                        bankAccountId = "001",
                        destinationBankAccount = "002",
                        amount = BigDecimal("850.00"),
                        reason = "rent payment 11/23",
                    ),
                )
                .expectSuccessfulExecution()
                .expectSingleEvent(
                    MoneyTransferRequestedEvent(
                        originBankAccountId = "001",
                        targetBankAccountId = "002",
                        amount = BigDecimal("850.00"),
                        reason = "rent payment 11/23",
                    ),
                )
        }
    }

    @Nested
    inner class CloseBankAccount {
        @Test
        fun `can close balanced non-closed bank account`(
            @Autowired fixture: CommandHandlingTestFixture<CloseBankAccountCommand>,
        ) {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal.ZERO,
                    ),
                )
                .`when`(
                    CloseBankAccountCommand(
                        bankAccountId = "001",
                    ),
                )
                .expectSuccessfulExecution()
                .expectSingleEvent(
                    BankAccountClosedEvent(
                        bankAccountId = "001",
                    ),
                )
        }

        @Test
        fun `fail to close non-balanced bank account`(
            @Autowired fixture: CommandHandlingTestFixture<CloseBankAccountCommand>,
        ) {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal("100.00"),
                    ),
                )
                .`when`(
                    CloseBankAccountCommand(
                        bankAccountId = "001",
                    ),
                )
                .expectException(BankAccountMustBeBalancedForCloseException::class.java)
        }

        @Test
        fun `fail to close already closed bank account`(
            @Autowired fixture: CommandHandlingTestFixture<CloseBankAccountCommand>,
        ) {
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = BigDecimal.ZERO,
                    ),
                    BankAccountClosedEvent(
                        bankAccountId = "001",
                    ),
                )
                .`when`(
                    CloseBankAccountCommand(
                        bankAccountId = "001",
                    ),
                )
                .expectException(BankAccountAlreadyClosedException::class.java)
        }
    }
}
