package coin.apply

import coin.identity.Person
import java.time.Instant
import java.time.LocalDate

data class Applicant(
  val firstName: String,
  val lastName: String,
  val dob: LocalDate,
  val ssnSerialNumber: String
)

data class LoanApplicationRequest(
  val applicant: Applicant,
  val loanAmount: Int,
  val termInMonths: Int
) {
  var requestSessionId: String? = ""
  var requestTimestamp: Instant = Instant.now()
  var requestIpAddress: String? = ""
  var requestUserAgent: String? = ""
}

data class Application(
  val id: String,
  val person: Person,
  val applicationType: ApplicationType,
  val status: ApplicationStatus,
  val createTime: Instant?
)

enum class ApplicationType {
  Loan
}

enum class ApplicationStatus {
  Pending,
  Declined,
  Closed,
  Withdrawn,
  Expired,
  Complete
}