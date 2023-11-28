package de.babsek.demo.axontesting.domain.exceptions

class BankAccountAlreadyExistingException(
    bankAccountId: String,
) : RuntimeException(
    "Another bank account with the id $bankAccountId is already existing."
)
