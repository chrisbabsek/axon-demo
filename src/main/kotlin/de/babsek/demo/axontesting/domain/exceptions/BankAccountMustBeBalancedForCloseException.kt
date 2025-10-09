package de.babsek.demo.axontesting.domain.exceptions

import java.math.BigDecimal

class BankAccountMustBeBalancedForCloseException(
    bankAccountId: String,
    remainingBalance: BigDecimal,
) : RuntimeException(
    "Bank account must be balanced to get closed, but current balance is '$remainingBalance': $bankAccountId",
)
