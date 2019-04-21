package coin.common

import com.auth0.jwt.interfaces.Payload
import io.ktor.auth.Principal

fun getApiPrincipalFromPayload(payload: Payload): ApiPrincipal {
  return ApiPrincipal(payload.getClaim("azp").asString())
}

data class ApiPrincipal(val clientId: String) : Principal

