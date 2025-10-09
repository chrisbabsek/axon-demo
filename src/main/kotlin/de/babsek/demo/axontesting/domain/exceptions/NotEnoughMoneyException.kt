package de.babsek.demo.axontesting.domain.exceptions

import java.math.BigDecimal

class NotEnoughMoneyException(
    bankAccountId: String,
    requestedAmount: BigDecimal,
    currentBalance: BigDecimal,
) : RuntimeException(
    "The bank account $bankAccountId balance is too low to transfer an amount of $requestedAmount! Current balance: $currentBalance",
)
