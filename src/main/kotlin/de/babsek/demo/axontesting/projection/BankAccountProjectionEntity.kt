package de.babsek.demo.axontesting.projection

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import de.babsek.demo.axontesting.domain.value.TransactionDetails
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.math.BigDecimal

@Table(name = "bank_account_projection")
@Entity
data class BankAccountProjectionEntity(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(name = "bank_account_id")
    val bankAccountId: String,

    @Column(name = "owner_name")
    val ownerName: String,

    @Column(name = "balance")
    val balance: BigDecimal,

    @Type(JsonBinaryType::class)
    @Column(name = "transactions", columnDefinition = "jsonb")
    val transactions: List<TransactionDetails> = emptyList(),
)
