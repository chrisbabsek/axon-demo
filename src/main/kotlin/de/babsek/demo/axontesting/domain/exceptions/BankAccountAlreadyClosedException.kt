package de.babsek.demo.axontesting.domain.exceptions

class BankAccountAlreadyClosedException(
    bankAccountId: String,
) : IllegalStateException("Bank account $bankAccountId is already closed.")
