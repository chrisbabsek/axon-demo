package de.babsek.demo.axontesting.domain.customer.exceptions

class BankCustomerAlreadyRegisteredException(
    bankCustomerId: String,
) : IllegalStateException("Bank customer '$bankCustomerId' is already registered.")
