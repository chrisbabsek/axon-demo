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
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class BankCustomerAggregate {

    @AggregateIdentifier
    private lateinit var bankCustomerId: String
    private lateinit var fullName: String
    private lateinit var contactInformation: ContactInformation
    private var identityVerified: Boolean = false
    private var active: Boolean = false

    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    @CommandHandler
    fun registerBankCustomer(command: RegisterBankCustomerCommand): String {
        if (this::bankCustomerId.isInitialized) {
            throw BankCustomerAlreadyRegisteredException(command.bankCustomerId)
        }

        val sanitizedFullName = command.fullName.trim()
        require(sanitizedFullName.isNotEmpty()) { "Full name must not be blank." }

        val sanitizedContactInformation = command.contactInformation.normalized()

        AggregateLifecycle.apply(
            BankCustomerRegisteredEvent(
                bankCustomerId = command.bankCustomerId,
                fullName = sanitizedFullName,
                contactInformation = sanitizedContactInformation,
            )
        )
        return command.bankCustomerId
    }

    @EventSourcingHandler
    fun on(event: BankCustomerRegisteredEvent) {
        bankCustomerId = event.bankCustomerId
        fullName = event.fullName
        contactInformation = event.contactInformation
        identityVerified = false
        active = true
    }

    @CommandHandler
    fun updateContactInformation(command: UpdateBankCustomerContactInformationCommand) {
        ensureActive()

        val sanitizedContactInformation = command.contactInformation.normalized()
        if (sanitizedContactInformation == contactInformation) {
            return
        }

        AggregateLifecycle.apply(
            BankCustomerContactInformationUpdatedEvent(
                bankCustomerId = bankCustomerId,
                contactInformation = sanitizedContactInformation,
            )
        )
    }

    @EventSourcingHandler
    fun on(event: BankCustomerContactInformationUpdatedEvent) {
        contactInformation = event.contactInformation
    }

    @CommandHandler
    fun verifyIdentity(command: VerifyBankCustomerIdentityCommand) {
        ensureActive()
        val sanitizedReference = command.verificationReference.trim()
        require(sanitizedReference.isNotEmpty()) { "Verification reference must not be blank." }
        if (identityVerified) {
            throw BankCustomerAlreadyVerifiedException(bankCustomerId)
        }

        AggregateLifecycle.apply(
            BankCustomerIdentityVerifiedEvent(
                bankCustomerId = bankCustomerId,
                verificationReference = sanitizedReference,
            )
        )
    }

    @EventSourcingHandler
    fun on(event: BankCustomerIdentityVerifiedEvent) {
        identityVerified = true
    }

    @CommandHandler
    fun deactivate(command: DeactivateBankCustomerCommand) {
        if (!active) {
            throw BankCustomerAlreadyDeactivatedException(bankCustomerId)
        }
        val sanitizedReason = command.reason.trim()
        require(sanitizedReason.isNotEmpty()) { "Reason must not be blank." }

        AggregateLifecycle.apply(
            BankCustomerDeactivatedEvent(
                bankCustomerId = bankCustomerId,
                reason = sanitizedReason,
            )
        )
    }

    @EventSourcingHandler
    fun on(event: BankCustomerDeactivatedEvent) {
        active = false
    }

    private fun ensureActive() {
        if (!active) {
            throw BankCustomerInactiveException(bankCustomerId)
        }
    }
}
