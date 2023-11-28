package de.babsek.demo.axontesting.remote

import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.OpenBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.TransferMoneyCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class BankAccountController(
    private val commandGateway: CommandGateway,
) {

    @PostMapping("bankaccounts")
    fun createBankAccount(@RequestBody request: BankAccountDto): String {
        return commandGateway.sendAndWait(
            OpenBankAccountCommand(
                bankAccountId = request.bankAccountId,
                ownerName = request.ownerName,
            ),
        )
    }

    data class BankAccountDto(
        val bankAccountId: String,
        val ownerName: String,
    )

    @PostMapping("bankaccounts/{bankAccountId}/payings")
    fun payInMoney(@PathVariable bankAccountId: String, @RequestBody request: PayInMoneyDto) {
        commandGateway.sendAndWait<Unit>(
            AcceptMoneyTransferCommand(
                bankAccountId = bankAccountId,
                amount = request.amount,
                reason = "pay in",
            ),
        )
    }

    data class PayInMoneyDto(
        val amount: Double,
    )

    @PostMapping("transfers")
    fun transferMoney(@RequestBody request: MoneyTransferDto) {
        commandGateway.sendAndWait<Unit>(
            TransferMoneyCommand(
                bankAccountId = request.originBankAccountId,
                destinationBankAccount = request.destinationBankAccountId,
                amount = request.amount,
                reason = request.reason,
            ),
        )
    }

    data class MoneyTransferDto(
        val originBankAccountId: String,
        val destinationBankAccountId: String,
        val amount: Double,
        val reason: String,
    )
}
