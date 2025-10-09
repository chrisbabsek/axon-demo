package de.babsek.demo.axontesting.domain.exceptions

class BankAccountAlreadyClosedException(
    bankAccountId: String,
) : RuntimeException(
    "Bank account $bankAccountId is already closed.",
)
