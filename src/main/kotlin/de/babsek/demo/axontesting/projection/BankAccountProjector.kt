package de.babsek.demo.axontesting.projection

import de.babsek.demo.axontesting.domain.events.*
import de.babsek.demo.axontesting.domain.value.TransactionDetails
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.stereotype.Component
import java.time.Instant

@ProcessingGroup("bankAccountProjection")
@Component
class BankAccountProjector(
    private val repository: BankAccountProjectionRepository,
) {

    @EventHandler
    fun on(event: BankAccountOpenedEvent, @Timestamp now: Instant) {
        repository.saveAndFlush(
            BankAccountProjectionEntity(
                bankAccountId = event.bankAccountId,
                ownerName = event.ownerName,
                balance = event.initialBalance,
                transactions = listOf(
                    TransactionDetails(
                        type = "opened",
                        date = now,
                        valuta = event.initialBalance,
                        details = "bank account opened",
                    )
            ),
            )
        )
    }

    @EventHandler
    fun on(event: MoneyTransferArrivedEvent, @Timestamp now: Instant) = updateProjection(event.bankAccountId) {
        copy(
            balance = balance + event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferArrived",
                date = now,
                valuta = event.amount,
                details = "payment arrived: ${event.reason}"
            )
        )
    }

    @EventHandler
    fun on(event: MoneyTransferRequestedEvent, @Timestamp now: Instant) = updateProjection(event.originBankAccountId) {
        copy(
            balance = balance - event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferRequested",
                date = now,
                valuta = -event.amount,
                details = "transfer to ${event.targetBankAccountId} requested: ${event.reason}"
            )
        )
    }

    @EventHandler
    fun on(event: MoneyTransferFailedEvent, @Timestamp now: Instant) = updateProjection(event.bankAccountId) {
        copy(
            balance = balance + event.amount,
            transactions = transactions + TransactionDetails(
                type = "moneyTransferRequested",
                date = now,
                valuta = event.amount,
                details = "transfer to ${event.targetBankAccountId} failed: ${event.errorMessage}"
            )
        )
    }

    @EventHandler
    fun on(event: BankAccountClosedEvent, @Timestamp now: Instant) = updateProjection(event.bankAccountId) {
        copy(
            transactions = transactions + TransactionDetails(
                type = "closed",
                date = now,
                valuta = 0.0,
                details = "bank account closed"
            )
        )
    }

    private fun updateProjection(
        bankAccountId: String,
        block: BankAccountProjectionEntity.() -> BankAccountProjectionEntity,
    ) {
        repository
            .findByBankAccountId(bankAccountId)
            ?.block()
            ?.apply(repository::saveAndFlush)
    }
}
