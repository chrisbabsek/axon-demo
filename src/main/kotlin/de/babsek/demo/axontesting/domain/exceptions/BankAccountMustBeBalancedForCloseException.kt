package de.babsek.demo.axontesting.domain.exceptions

class BankAccountMustBeBalancedForCloseException(
    bankAccountId: String,
    remainingBalance: Double,
) : RuntimeException(
    "Bank account must be balanced to get closed, but current balance is '$remainingBalance': $bankAccountId"
)
