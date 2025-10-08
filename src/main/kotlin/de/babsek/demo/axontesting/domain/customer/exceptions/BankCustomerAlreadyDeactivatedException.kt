package de.babsek.demo.axontesting.domain.customer.exceptions

class BankCustomerAlreadyDeactivatedException(
    bankCustomerId: String,
) : IllegalStateException("Bank customer '$bankCustomerId' is already deactivated.")
