package de.babsek.demo.axontesting.domain.customer.value

import java.util.Locale

/**
 * Immutable value object that captures the essential contact information for a bank customer.
 */
data class ContactInformation(
    val emailAddress: String,
    val phoneNumber: String?,
    val postalAddress: String,
) {
    init {
        require(emailAddress.isNotBlank()) { "Email address must not be blank." }
        require('@' in emailAddress) { "Email address must contain '@'." }
        require(postalAddress.isNotBlank()) { "Postal address must not be blank." }
    }

    /**
     * Returns a sanitized copy of this contact information with normalized email casing and whitespace-free phone number.
     */
    fun normalized(): ContactInformation = copy(
        emailAddress = emailAddress.lowercase(Locale.getDefault()),
        phoneNumber = phoneNumber?.filterNot(Char::isWhitespace),
        postalAddress = postalAddress.trim(),
    )
}
