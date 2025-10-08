package de.babsek.demo.axontesting.domain.customer.exceptions

class BankCustomerAlreadyVerifiedException(
    bankCustomerId: String,
) : IllegalStateException("Bank customer '$bankCustomerId' is already verified.")
