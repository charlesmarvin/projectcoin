package coin.apply

import coin.common.servicemodule
import coin.identity.InMemoryPersonRepository
import coin.identity.InMemoryPersonService
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun io.ktor.application.Application.apply() {
  servicemodule()
  val personService = InMemoryPersonService(
    personRepository = InMemoryPersonRepository()
  )
  val applicationService = InMemoryApplicationService(
    personService = personService,
    applicationRepository = InMemoryApplicationRepository(),
    loanApplicationRequestRepository = InMemoryApplicationRequestRepository()
  )

  routing {
    loanApplication(applicationService)
  }
}
