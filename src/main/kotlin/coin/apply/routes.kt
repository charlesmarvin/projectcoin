package coin.apply

import coin.common.ApiPrincipal
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.callId
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.userAgent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Routing.loanApplication(applicationService: ApplicationService) {

  route("/v1/applications") {
    post {
      val loanApplicationRequest = call.receive<LoanApplicationRequest>()
      loanApplicationRequest.requestIpAddress = call.request.origin.remoteHost
      loanApplicationRequest.requestUserAgent = call.request.userAgent()
      loanApplicationRequest.requestSessionId = call.callId
      try {
        val application = applicationService.createLoanApplication(loanApplicationRequest)
        call.respond(HttpStatusCode.Created, application)
      } catch (ex1: CouldNotResolvePersonFromApplicantException) {
        call.application.environment.log.warn("Error creating Application", ex1)
        call.respond(HttpStatusCode.BadRequest)
      }
    }
    authenticate {
      route("/self") {
        get {
          when (val principal = call.authentication.principal) {
            is ApiPrincipal -> call.respond(applicationService.getApplicationsByPerson(principal))
            else -> call.respond(HttpStatusCode.Forbidden)
          }

        }
      }
      route("/{id}") {
        get {
          val applicationId = call.parameters["id"]!!
          when (val maybeApplication = applicationService.getApplication(applicationId)) {
            is Application -> call.respond(maybeApplication)
            else -> call.respond(HttpStatusCode.BadRequest)
          }
        }
      }
      route("/requests") {
        get {
          call.respond(HttpStatusCode.OK, applicationService.getLoanApplicationRequests())
        }
      }
    }
  }
}