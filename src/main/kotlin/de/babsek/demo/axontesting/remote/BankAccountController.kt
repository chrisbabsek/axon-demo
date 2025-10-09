package de.babsek.demo.axontesting.remote

import com.opencqrs.framework.command.CommandRouter
import de.babsek.demo.axontesting.domain.commands.AcceptMoneyTransferCommand
import de.babsek.demo.axontesting.domain.commands.CloseBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.OpenBankAccountCommand
import de.babsek.demo.axontesting.domain.commands.TransferMoneyCommand
import de.babsek.demo.axontesting.projection.BankAccountProjectionRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class BankAccountController(
    private val commandRouter: CommandRouter,
    private val bankAccountProjectionRepository: BankAccountProjectionRepository,
) {

    @GetMapping("bankaccounts")
    fun findAll(): List<BankAccountDto> =
        bankAccountProjectionRepository
            .findAll()
            .map { it.toDto() }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("bankaccounts")
    fun openBankAccount(@Valid @RequestBody request: CreateBankAccountDto): String {
        val command = OpenBankAccountCommand(
            bankAccountId = request.bankAccountId,
            ownerName = request.ownerName,
        )
        return commandRouter.send(command)
    }

    data class CreateBankAccountDto(
        @field:NotBlank
        val bankAccountId: String,
        @field:NotBlank
        val ownerName: String,
    )

    @GetMapping("bankaccounts/{bankAccountId}")
    fun findById(@PathVariable bankAccountId: String): BankAccountDto? =
        bankAccountProjectionRepository
            .findByBankAccountId(bankAccountId)
            ?.toDto()

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("bankaccounts/{bankAccountId}")
    fun closeBankAccount(@PathVariable bankAccountId: String) {
        commandRouter.send<Unit>(
            CloseBankAccountCommand(
                bankAccountId = bankAccountId,
            ),
        )
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("bankaccounts/{bankAccountId}/payments")
    fun payInMoney(@PathVariable bankAccountId: String, @Valid @RequestBody request: PayInMoneyDto) {
        commandRouter.send<Unit>(
            AcceptMoneyTransferCommand(
                bankAccountId = bankAccountId,
                amount = request.amount,
                reason = "pay in",
            ),
        )
    }

    data class PayInMoneyDto(
        @field:DecimalMin("0.01")
        val amount: BigDecimal,
    )

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("transfers")
    fun transferMoney(@Valid @RequestBody request: MoneyTransferDto) {
        commandRouter.send<Unit>(
            TransferMoneyCommand(
                bankAccountId = request.originBankAccountId,
                destinationBankAccount = request.destinationBankAccountId,
                amount = request.amount,
                reason = request.reason,
            ),
        )
    }

    data class MoneyTransferDto(
        @field:NotBlank
        val originBankAccountId: String,
        @field:NotBlank
        val destinationBankAccountId: String,
        @field:DecimalMin("0.01")
        val amount: BigDecimal,
        @field:NotBlank
        val reason: String,
    )

    private fun de.babsek.demo.axontesting.projection.BankAccountProjectionEntity.toDto() = BankAccountDto(
        bankAccountId = bankAccountId,
        ownerName = ownerName,
        balance = balance,
        transactions = transactions,
    )
}
