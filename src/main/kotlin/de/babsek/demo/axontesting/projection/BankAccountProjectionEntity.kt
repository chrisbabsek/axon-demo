package de.babsek.demo.axontesting.projection

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import de.babsek.demo.axontesting.domain.value.TransactionDetails
import jakarta.persistence.*
import org.hibernate.annotations.Type


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
    val balance: Double,

    @Type(JsonBinaryType::class)
    @Column(name = "transactions", columnDefinition = "jsonb")
    val transactions: List<TransactionDetails> = emptyList(),
)
