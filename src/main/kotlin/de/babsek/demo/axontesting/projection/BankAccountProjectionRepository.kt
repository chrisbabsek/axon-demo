package de.babsek.demo.axontesting.projection

import org.springframework.data.jpa.repository.JpaRepository

interface BankAccountProjectionRepository : JpaRepository<BankAccountProjectionEntity, Int> {
    fun findByBankAccountId(bankAccountId: String): BankAccountProjectionEntity?
}
