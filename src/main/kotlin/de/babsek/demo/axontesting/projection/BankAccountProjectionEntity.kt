package de.babsek.demo.axontesting.projection

import jakarta.persistence.*


@Table(name = "bankaccountprojection")
@Entity
data class BankAccountProjectionEntity(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(name = "bankaccountid")
    val bankAccountId: String,
    @Column(name = "balance")
    val balance: Double
)
