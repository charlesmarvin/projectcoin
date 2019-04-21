package coin.common

import com.auth0.jwk.JwkProviderBuilder
import com.codahale.metrics.JmxReporter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.features.callIdMdc
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.metrics.Metrics
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import java.util.concurrent.TimeUnit

@KtorExperimentalAPI
fun io.ktor.application.Application.servicemodule() {
  val jwtIssuer = environment.config.property("jwt.issuer").getString()
  val jwtAudience = environment.config.property("jwt.audience").getString()
  val jwtRealm = environment.config.property("jwt.realm").getString()

  install(Authentication) {
    jwt {
      realm = jwtRealm
      verifier(JwkProviderBuilder(jwtIssuer).build()) //TODO consider further configuration
      validate { credential ->
        if (credential.payload.audience.contains(jwtAudience)) getApiPrincipalFromPayload(credential.payload) else null
      }
    }
  }
  install(CallId) {
    generate {
      IdProvider.get()
    }
  }
  install(CallLogging) {
    callIdMdc("X-Request-ID")
  }
  install(ContentNegotiation) {
    jackson {
      disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      registerModule(JavaTimeModule())
    }
  }
  install(DefaultHeaders)
  install(Metrics) {
    JmxReporter.forRegistry(registry)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
      .start()
  }
  install(StatusPages) {
    exception<Throwable> {
      call.respond(HttpStatusCode.InternalServerError)
    }
  }
  install(XForwardedHeaderSupport)

  routing {
    get("/health_check") {
      // Check databases/other services.
      call.respond(HttpStatusCode.OK)
    }
  }
}

