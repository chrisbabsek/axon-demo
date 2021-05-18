package de.babsek.demo.axontesting.projection

import de.babsek.demo.axontesting.domain.events.BankAccountOpenedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferArrivedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferFailedEvent
import de.babsek.demo.axontesting.domain.events.MoneyTransferRequestedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@ProcessingGroup("bankAccountProjection")
@Component
class BankAccountProjector(
    private val repository: BankAccountProjectionRepository
) {

    private fun updateBalance(bankAccountId: String, balanceChanger: (Double) -> Double) {
        repository
            .findByBankAccountId(bankAccountId)
            ?.let { it.copy(balance = balanceChanger(it.balance)) }
            ?.apply(repository::saveAndFlush)
    }
}