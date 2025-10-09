package de.babsek.demo.axontesting.projection

import com.opencqrs.esdb.client.Event
import com.opencqrs.framework.eventhandler.EventHandling
import de.babsek.demo.axontesting.configuration.ProcessingGroups
import de.babsek.demo.axontesting.domain.events.*
import de.babsek.demo.axontesting.domain.value.TransactionDetails
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BankAccountProjector(
    private val repository: BankAccountProjectionRepository,
) {

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: BankAccountOpenedEvent, rawEvent: Event) {
        repository.saveAndFlush(
            BankAccountProjectionEntity(
                bankAccountId = event.bankAccountId,
                ownerName = event.ownerName,
                balance = event.initialBalance,
                transactions = listOf(
                    TransactionDetails(
                        type = "opened",
                        date = rawEvent.time(),
                        valuta = event.initialBalance,
                        details = "bank account opened",
                    ),
                ),
            ),
        )
    }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: MoneyTransferArrivedEvent, rawEvent: Event) = updateProjection(event.bankAccountId) {
        copy(
            balance = balance + event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferArrived",
                date = rawEvent.time(),
                valuta = event.amount,
                details = "payment arrived: ${event.reason}",
            ),
        )
    }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: MoneyTransferRequestedEvent, rawEvent: Event) =
        updateProjection(event.originBankAccountId) {
            copy(
                balance = balance - event.amount,
                transactions = transactions + TransactionDetails(
                    type = "moneyTransferRequested",
                    date = rawEvent.time(),
                    valuta = -event.amount,
                    details = "transfer to ${event.targetBankAccountId} requested: ${event.reason}",
                ),
            )
        }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: MoneyTransferFailedEvent, rawEvent: Event) = updateProjection(event.bankAccountId) {
        copy(
            balance = balance + event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferFailed",
                date = rawEvent.time(),
                valuta = event.amount,
                details = "transfer to ${event.targetBankAccountId} failed: ${event.errorMessage}",
            ),
        )
    }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: BankAccountClosedEvent, rawEvent: Event) = updateProjection(event.bankAccountId) {
        copy(
            transactions = transactions + TransactionDetails(
                type = "closed",
                date = rawEvent.time(),
                valuta = 0.0,
                details = "bank account closed",
            ),
        )
    }

    private fun updateProjection(
        bankAccountId: String,
        block: BankAccountProjectionEntity.() -> BankAccountProjectionEntity,
    ) {
        repository
            .findByBankAccountId(bankAccountId)
            ?.block()
            ?.let(repository::saveAndFlush)
    }
}
