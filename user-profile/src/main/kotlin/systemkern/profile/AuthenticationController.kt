package systemkern.profile

import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@RestController
internal class AuthenticationController(
    val service: AuthenticationService
) {
    @PostMapping("/login")
    internal fun login(auth: Authentication,
                       @RequestHeader password: String
    ): AuthenticationResponse {
        return service.authenticationProcess(auth, password)
    }

    @PostMapping("/logout")
    internal fun logout(@RequestHeader authorization: String,
                        request: HttpServletRequest) {
        service.deleteToken(UUID.fromString(authorization.split(" ")[1]))
        request.session.invalidate()
    }
}

internal data class AuthenticationResponse(
    val token: UUID,
    val username: String,
    val userId: UUID,
    val validUntil: LocalDateTime
)

@ResponseStatus(HttpStatus.NOT_FOUND)
internal class UserNotFoundException(message: String?) : RuntimeException(message)