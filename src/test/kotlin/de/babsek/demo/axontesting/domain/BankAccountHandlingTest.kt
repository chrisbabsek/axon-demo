package de.babsek.demo.axontesting.domain

import com.opencqrs.framework.command.CommandHandlingTest
import com.opencqrs.framework.command.CommandHandlingTestFixture
import com.opencqrs.framework.command.CommandSubjectAlreadyExistsException
import de.babsek.demo.axontesting.domain.commands.*
import de.babsek.demo.axontesting.domain.events.*
import de.babsek.demo.axontesting.domain.exceptions.BankAccountAlreadyClosedException
import de.babsek.demo.axontesting.domain.exceptions.BankAccountMustBeBalancedForCloseException
import de.babsek.demo.axontesting.domain.exceptions.NotEnoughMoneyException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

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
                .expectSuccessfulExecution()
                .expectSingleEvent(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 0.0,
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
                        initialBalance = 0.0,
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
                        initialBalance = 0.0,
                    ),
                )
                .`when`(
                    AcceptMoneyTransferCommand(
                        bankAccountId = "001",
                        amount = 2500.0,
                        reason = "salary 11/23",
                    ),
                )
                .expectSuccessfulExecution()
                .expectSingleEvent(
                    MoneyTransferArrivedEvent(
                        bankAccountId = "001",
                        amount = 2500.0,
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
                        initialBalance = 1000.0,
                    ),
                )
                .`when`(
                    TransferMoneyCommand(
                        bankAccountId = "001",
                        destinationBankAccount = "002",
                        amount = 1001.0,
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
                        initialBalance = 1000.0,
                    ),
                )
                .`when`(
                    TransferMoneyCommand(
                        bankAccountId = "001",
                        destinationBankAccount = "002",
                        amount = 850.0,
                        reason = "rent payment 11/23",
                    ),
                )
                .expectSuccessfulExecution()
                .expectSingleEvent(
                    MoneyTransferRequestedEvent(
                        originBankAccountId = "001",
                        targetBankAccountId = "002",
                        amount = 850.0,
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
                        initialBalance = 0.0,
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
                        initialBalance = 100.0,
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
                        initialBalance = 0.0,
                    ),
                )
                .andGiven(
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

    @Nested
    inner class InformFailedMoneyTransfer {

        @Test
        fun `restores balance after failed transfer`(
            @Autowired fixture: CommandHandlingTestFixture<InformFailedMoneyTransferCommand>,
        ) {
            val reader = UUID.randomUUID().toString()
            fixture
                .given(
                    BankAccountOpenedEvent(
                        bankAccountId = "001",
                        ownerName = "Ted Tester",
                        initialBalance = 100.0,
                    ),
                )
                .andGiven(
                    MoneyTransferRequestedEvent(
                        originBankAccountId = "001",
                        targetBankAccountId = reader,
                        amount = 50.0,
                        reason = "gift",
                    ),
                )
                .`when`(
                    InformFailedMoneyTransferCommand(
                        bankAccountId = "001",
                        targetBankAccountId = reader,
                        amount = 50.0,
                        errorMessage = "insufficient funds",
                    ),
                )
                .expectSuccessfulExecution()
                .expectSingleEvent(
                    MoneyTransferFailedEvent(
                        bankAccountId = "001",
                        targetBankAccountId = reader,
                        amount = 50.0,
                        errorMessage = "insufficient funds",
                    ),
                )
        }
    }
}
