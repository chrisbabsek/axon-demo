package de.babsek.demo.axontesting.remote

import com.opencqrs.framework.command.CommandRouter
import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.CloseBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.OpenBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.TransferMoneyCommand
import de.babsek.demo.axontesting.projection.BankAccountProjectionEntity
import de.babsek.demo.axontesting.projection.BankAccountProjectionRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class BankAccountController(
    private val commandRouter: CommandRouter,
    private val bankAccountProjectionRepository: BankAccountProjectionRepository,
) {

    @GetMapping("bankaccounts")
    fun findAll(): List<BankAccountDto> {
        return bankAccountProjectionRepository
            .findAll()
            .map { it.toDto() }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("bankaccounts")
    fun openBankAccount(
        @RequestBody request: CreateBankAccountDto,
        httpRequest: HttpServletRequest,
    ): String {
        return commandRouter.send(
            OpenBankAccountCommand(
                bankAccountId = request.bankAccountId,
                ownerName = request.ownerName,
            ),
            metaData(httpRequest),
        )
    }

    data class CreateBankAccountDto(
        val bankAccountId: String,
        val ownerName: String,
    )

    @GetMapping("bankaccounts/{bankAccountId}")
    fun findById(@PathVariable bankAccountId: String): BankAccountDto? {
        return bankAccountProjectionRepository
            .findByBankAccountId(bankAccountId)
            ?.toDto()
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("bankaccounts/{bankAccountId}")
    fun closeBankAccount(
        @PathVariable bankAccountId: String,
        httpRequest: HttpServletRequest,
    ) {
        commandRouter.send<Unit>(
            CloseBankAccountCommand(
                bankAccountId = bankAccountId,
            ),
            metaData(httpRequest),
        )
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("bankaccounts/{bankAccountId}/payments")
    fun payInMoney(
        @PathVariable bankAccountId: String,
        @RequestBody request: PayInMoneyDto,
        httpRequest: HttpServletRequest,
    ) {
        commandRouter.send<Unit>(
            AcceptMoneyTransferCommand(
                bankAccountId = bankAccountId,
                amount = request.amount,
                reason = "pay in",
            ),
            metaData(httpRequest),
        )
    }

    data class PayInMoneyDto(
        val amount: Double,
    )

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("transfers")
    fun transferMoney(
        @RequestBody request: MoneyTransferDto,
        httpRequest: HttpServletRequest,
    ) {
        commandRouter.send<Unit>(
            TransferMoneyCommand(
                bankAccountId = request.originBankAccountId,
                destinationBankAccount = request.destinationBankAccountId,
                amount = request.amount,
                reason = request.reason,
            ),
            metaData(httpRequest),
        )
    }

    data class MoneyTransferDto(
        val originBankAccountId: String,
        val destinationBankAccountId: String,
        val amount: Double,
        val reason: String,
    )

    private fun BankAccountProjectionEntity.toDto() = BankAccountDto(
        bankAccountId = bankAccountId,
        ownerName = ownerName,
        balance = balance,
        transactions = transactions,
    )

    private fun metaData(httpRequest: HttpServletRequest): Map<String, String> =
        mapOf("request-uri" to httpRequest.requestURI)
}
