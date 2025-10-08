package de.babsek.demo.axontesting.domain.customer

import de.babsek.demo.axontesting.domain.customer.commands.DeactivateBankCustomerCommand
import de.babsek.demo.axontesting.domain.customer.commands.RegisterBankCustomerCommand
import de.babsek.demo.axontesting.domain.customer.commands.UpdateBankCustomerContactInformationCommand
import de.babsek.demo.axontesting.domain.customer.commands.VerifyBankCustomerIdentityCommand
import de.babsek.demo.axontesting.domain.customer.events.BankCustomerContactInformationUpdatedEvent
import de.babsek.demo.axontesting.domain.customer.events.BankCustomerDeactivatedEvent
import de.babsek.demo.axontesting.domain.customer.events.BankCustomerIdentityVerifiedEvent
import de.babsek.demo.axontesting.domain.customer.events.BankCustomerRegisteredEvent
import de.babsek.demo.axontesting.domain.customer.exceptions.BankCustomerAlreadyDeactivatedException
import de.babsek.demo.axontesting.domain.customer.exceptions.BankCustomerAlreadyRegisteredException
import de.babsek.demo.axontesting.domain.customer.exceptions.BankCustomerAlreadyVerifiedException
import de.babsek.demo.axontesting.domain.customer.exceptions.BankCustomerInactiveException
import de.babsek.demo.axontesting.domain.customer.value.ContactInformation
import org.axonframework.extension.kotlin.test.aggregateTestFixture
import org.axonframework.extension.kotlin.test.expectException
import org.axonframework.extension.kotlin.test.whenever
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BankCustomerAggregateTest {

    private val fixture = aggregateTestFixture<BankCustomerAggregate>()
    private val registeredEvent = BankCustomerRegisteredEvent(
        bankCustomerId = "C-001",
        fullName = "Alice Example",
        contactInformation = ContactInformation(
            emailAddress = "alice@example.com",
            phoneNumber = "+491701234567",
            postalAddress = "Example Street 1, 12345 Sampletown",
        ),
    )

    @Nested
    inner class RegisterBankCustomer {
        @Test
        fun `successfully registers a new bank customer`() {
            fixture
                .givenNoPriorActivity()
                .whenever(
                    RegisterBankCustomerCommand(
                        bankCustomerId = "C-001",
                        fullName = "  Alice Example  ",
                        contactInformation = ContactInformation(
                            emailAddress = "Alice@Example.COM",
                            phoneNumber = "+49 170 1234567",
                            postalAddress = " Example Street 1, 12345 Sampletown ",
                        ),
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(registeredEvent)
        }

        @Test
        fun `rejects registering an already known customer`() {
            fixture
                .given(registeredEvent)
                .whenever(
                    RegisterBankCustomerCommand(
                        bankCustomerId = "C-001",
                        fullName = "Alice Example",
                        contactInformation = ContactInformation(
                            emailAddress = "alice@example.com",
                            phoneNumber = "+491701234567",
                            postalAddress = "Example Street 1, 12345 Sampletown",
                        ),
                    )
                )
                .expectException(BankCustomerAlreadyRegisteredException::class)
        }

        @Test
        fun `rejects registration with blank full name`() {
            fixture
                .givenNoPriorActivity()
                .whenever(
                    RegisterBankCustomerCommand(
                        bankCustomerId = "C-002",
                        fullName = " \t ",
                        contactInformation = registeredEvent.contactInformation,
                    )
                )
                .expectException(IllegalArgumentException::class)
        }
    }

    @Nested
    inner class UpdateContactInformation {
        @Test
        fun `updates contact information for an active customer`() {
            val updatedContact = ContactInformation(
                emailAddress = "alice.new@example.com",
                phoneNumber = "+49 170 7654321",
                postalAddress = "New Street 2, 54321 Othercity",
            )
            fixture
                .given(registeredEvent)
                .whenever(
                    UpdateBankCustomerContactInformationCommand(
                        bankCustomerId = "C-001",
                        contactInformation = updatedContact,
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    BankCustomerContactInformationUpdatedEvent(
                        bankCustomerId = "C-001",
                        contactInformation = updatedContact.normalized(),
                    )
                )
        }

        @Test
        fun `does not emit event when contact information unchanged`() {
            fixture
                .given(registeredEvent)
                .whenever(
                    UpdateBankCustomerContactInformationCommand(
                        bankCustomerId = "C-001",
                        contactInformation = registeredEvent.contactInformation,
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectNoEvents()
        }

        @Test
        fun `rejects updates for inactive customers`() {
            fixture
                .given(registeredEvent)
                .andGiven(
                    BankCustomerDeactivatedEvent(
                        bankCustomerId = "C-001",
                        reason = "customer request",
                    )
                )
                .whenever(
                    UpdateBankCustomerContactInformationCommand(
                        bankCustomerId = "C-001",
                        contactInformation = registeredEvent.contactInformation,
                    )
                )
                .expectException(BankCustomerInactiveException::class)
        }
    }

    @Nested
    inner class VerifyIdentity {
        @Test
        fun `marks an active customer as verified`() {
            fixture
                .given(registeredEvent)
                .whenever(
                    VerifyBankCustomerIdentityCommand(
                        bankCustomerId = "C-001",
                        verificationReference = "videoident-123",
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    BankCustomerIdentityVerifiedEvent(
                        bankCustomerId = "C-001",
                        verificationReference = "videoident-123",
                    )
                )
        }

        @Test
        fun `rejects duplicate verifications`() {
            fixture
                .given(registeredEvent)
                .andGiven(
                    BankCustomerIdentityVerifiedEvent(
                        bankCustomerId = "C-001",
                        verificationReference = "videoident-123",
                    )
                )
                .whenever(
                    VerifyBankCustomerIdentityCommand(
                        bankCustomerId = "C-001",
                        verificationReference = "videoident-456",
                    )
                )
                .expectException(BankCustomerAlreadyVerifiedException::class)
        }

        @Test
        fun `rejects verification for inactive customers`() {
            fixture
                .given(registeredEvent)
                .andGiven(
                    BankCustomerDeactivatedEvent(
                        bankCustomerId = "C-001",
                        reason = "customer request",
                    )
                )
                .whenever(
                    VerifyBankCustomerIdentityCommand(
                        bankCustomerId = "C-001",
                        verificationReference = "videoident-123",
                    )
                )
                .expectException(BankCustomerInactiveException::class)
        }

        @Test
        fun `rejects verification with blank reference`() {
            fixture
                .given(registeredEvent)
                .whenever(
                    VerifyBankCustomerIdentityCommand(
                        bankCustomerId = "C-001",
                        verificationReference = "  ",
                    )
                )
                .expectException(IllegalArgumentException::class)
        }
    }

    @Nested
    inner class DeactivateCustomer {
        @Test
        fun `deactivates an active customer`() {
            fixture
                .given(registeredEvent)
                .whenever(
                    DeactivateBankCustomerCommand(
                        bankCustomerId = "C-001",
                        reason = "customer request",
                    )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                    BankCustomerDeactivatedEvent(
                        bankCustomerId = "C-001",
                        reason = "customer request",
                    )
                )
        }

        @Test
        fun `rejects deactivation when already inactive`() {
            fixture
                .given(registeredEvent)
                .andGiven(
                    BankCustomerDeactivatedEvent(
                        bankCustomerId = "C-001",
                        reason = "customer request",
                    )
                )
                .whenever(
                    DeactivateBankCustomerCommand(
                        bankCustomerId = "C-001",
                        reason = "duplicate request",
                    )
                )
                .expectException(BankCustomerAlreadyDeactivatedException::class)
        }

        @Test
        fun `rejects deactivation with blank reason`() {
            fixture
                .given(registeredEvent)
                .whenever(
                    DeactivateBankCustomerCommand(
                        bankCustomerId = "C-001",
                        reason = "  ",
                    )
                )
                .expectException(IllegalArgumentException::class)
        }
    }
}
