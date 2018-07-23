package systemkern.profile

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController("/auth")
internal class AuthenticationController(
    val service: AuthenticationService,
    val sessionTimeOut: Duration
) {

    @PostMapping
    internal fun login(auth: Authentication, @RequestHeader password: String
    ): AuthenticationResponse {
        val passwordEncoder = BCryptPasswordEncoder()
        try {
            val user = service.findByUsername(auth.principal.toString())
            if (!passwordEncoder.matches(password, user.password))
                throw UserNotFoundException("UserNotFoundException")
            val token: UUID = UUID.fromString(auth.credentials.toString())
            val validUntil = LocalDateTime.now().plusMinutes(sessionTimeOut.toMinutes())
            val authResp = AuthenticationResponse(
                token = token,
                username = user.username,
                userId = user.id,
                validUntil = validUntil)
            service.saveToken(token, authResp)
            return authResp
        } catch (e: EmptyResultDataAccessException) {
            throw UserNotFoundException("UserNotFoundException")
        }
    }

    @DeleteMapping("{id}")
    internal fun logout(@PathVariable id: UUID, request: HttpServletRequest) {
        service.deleteToken(id)
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