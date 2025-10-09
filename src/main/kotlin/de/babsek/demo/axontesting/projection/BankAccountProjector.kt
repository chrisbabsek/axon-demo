package de.babsek.demo.axontesting.projection

import com.opencqrs.framework.eventhandler.EventHandling
import de.babsek.demo.axontesting.configuration.ProcessingGroups
import de.babsek.demo.axontesting.domain.events.BankAccountClosedEvent
import de.babsek.demo.axontesting.domain.events.BankAccountOpenedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferArrivedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferFailedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import de.babsek.demo.axontesting.domain.value.TransactionDetails
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

@Component
class BankAccountProjector(
    private val repository: BankAccountProjectionRepository,
) {

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: BankAccountOpenedEvent) {
        repository.save(
            BankAccountProjectionEntity(
                bankAccountId = event.bankAccountId,
                ownerName = event.ownerName,
                balance = event.initialBalance,
                transactions = listOf(
                    TransactionDetails(
                        type = "opened",
                        date = Instant.now(),
                        valuta = event.initialBalance,
                        details = "bank account opened",
                    ),
                ),
            ),
        )
    }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: MoneyTransferArrivedEvent) = updateProjection(event.bankAccountId) {
        copy(
            balance = balance + event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferArrived",
                date = Instant.now(),
                valuta = event.amount,
                details = "payment arrived: ${event.reason}",
            ),
        )
    }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: MoneyTransferRequestedEvent) = updateProjection(event.originBankAccountId) {
        copy(
            balance = balance - event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferRequested",
                date = Instant.now(),
                valuta = event.amount.negate(),
                details = "transfer to ${event.targetBankAccountId} requested: ${event.reason}",
            ),
        )
    }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: MoneyTransferFailedEvent) = updateProjection(event.bankAccountId) {
        copy(
            balance = balance + event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferFailed",
                date = Instant.now(),
                valuta = event.amount,
                details = "transfer to ${event.targetBankAccountId} failed: ${event.errorMessage}",
            ),
        )
    }

    @EventHandling(ProcessingGroups.READ_MODEL_PROJECTION)
    @Transactional
    fun on(event: BankAccountClosedEvent) = updateProjection(event.bankAccountId) {
        copy(
            transactions = transactions + TransactionDetails(
                type = "closed",
                date = Instant.now(),
                valuta = BigDecimal.ZERO,
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
            ?.let(repository::save)
    }
}
