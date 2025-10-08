package de.babsek.demo.axontesting.domain.customer.exceptions

class BankCustomerInactiveException(
    bankCustomerId: String,
) : IllegalStateException("Bank customer '$bankCustomerId' is inactive.")
