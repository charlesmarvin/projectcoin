package coin.identity

import java.time.LocalDate

data class Person(
  val id: String,
  val firstName: String,
  val lastName: String,
  val dob: LocalDate,
  val nationalIdentifier: NationalIdentifier,
  val contactInfo: ContactInfo
)

data class NationalIdentifier(
  val number: String,
  val type: String
)

data class ContactInfo(
  val primaryAddress: Address,
  val mailingAddress: Address,
  val billingAddress: Address,
  val phoneNumber: PhoneNumber
)

data class PhoneNumber(
  val number: String,
  val type: PhoneNumberType
)

enum class PhoneNumberType {
  Voip,
  Mobile,
  LandLine
}

data class Address(
  val addressLine1: String,
  val addressLine2: String,
  val addressLine3: String,
  val city: String,
  val state: String,
  val country: String,
  val postalCode: String
)