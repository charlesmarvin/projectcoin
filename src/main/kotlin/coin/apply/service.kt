package coin.apply

import coin.common.ApiPrincipal
import coin.common.IdProvider
import coin.common.Repository
import coin.identity.PersonService
import io.ktor.request.ApplicationRequest
import java.time.Instant

interface ApplicationService {
  fun getApplication(applicationId: String): Application?
  fun createLoanApplication(applicationRequest: LoanApplicationRequest): Application
  fun getApplicationsByPerson(principal: ApiPrincipal): List<Application>
  fun getLoanApplicationRequests(): List<LoanApplicationRequest>
}

class InMemoryApplicationService(
  private val applicationRepository: ApplicationRepository,
  private val loanApplicationRequestRepository: ApplicationRequestRepository,
  private val personService: PersonService
) : ApplicationService {
  override fun getLoanApplicationRequests(): List<LoanApplicationRequest> {
    return loanApplicationRequestRepository.findAll().toList()
  }

  override fun getApplicationsByPerson(principal: ApiPrincipal): List<Application> {
    return applicationRepository.findAll().filter { app -> app.person.id == principal.clientId }
  }

  override fun createLoanApplication(applicationRequest: LoanApplicationRequest): Application {
    val id = IdProvider.get()
    loanApplicationRequestRepository.save(applicationRequest)
    val applicant = applicationRequest.applicant
    val person = personService.resolve(
      firstName = applicant.firstName,
      lastName = applicant.lastName,
      dob = applicant.dob,
      ssnSerialNumber = applicant.ssnSerialNumber
    )
    when (person) {
      null -> throw CouldNotResolvePersonFromApplicantException()
      else -> {
        val application =
          Application(id, person, ApplicationType.Loan, ApplicationStatus.Pending, Instant.now())
        applicationRepository.save(application)
        return application
      }
    }
  }

  override fun getApplication(applicationId: String): Application? {
    return applicationRepository.findById(applicationId)
  }
}

class CouldNotResolvePersonFromApplicantException(message: String = "Could not resolve person from applicant info.") :
  RuntimeException(message)

interface ApplicationRequestRepository : Repository<LoanApplicationRequest, String>

interface ApplicationRepository : Repository<Application, String>

class InMemoryApplicationRepository : ApplicationRepository {
  private val repository: MutableMap<String, Application> = mutableMapOf()

  override fun findAll(): Collection<Application> {
    return repository.values
  }

  override fun findById(id: String): Application? {
    return repository[id]
  }

  override fun save(entity: Application) {
    repository[entity.id] = entity
  }

}

class InMemoryApplicationRequestRepository : ApplicationRequestRepository {
  private val repository: MutableMap<String, LoanApplicationRequest> = mutableMapOf()


  override fun findAll(): Collection<LoanApplicationRequest> {
    return repository.values
  }

  override fun findById(id: String): LoanApplicationRequest? {
    return repository[id]
  }

  override fun save(entity: LoanApplicationRequest) {
    repository[entity.requestSessionId!!] = entity
  }

}