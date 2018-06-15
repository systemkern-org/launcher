package systemkern.profile

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@RestController
internal class AuthenticationController(
    val service: AuthenticationService
) {
    @PostMapping("/login")
    internal fun login(auth: Authentication, @RequestHeader password: String): AuthenticationResponse {
        val passwordEncoder = BCryptPasswordEncoder()
        try {
            val user = service.findByUsername(auth.principal.toString())
            if (!passwordEncoder.matches(password, user.password))
                throw UserNotFoundException("UserNotFoundException")
            val token: UUID = UUID.fromString(auth.credentials.toString())
            val validUntil = LocalDateTime.now().plusMinutes(30)
            val authResp = AuthenticationResponse(
                token = token,
                username = user.username,
                userId = user.id,
                validUntil = validUntil
            )
            AuthenticationService.saveToken(token, authResp)
            return authResp
        } catch (e: EmptyResultDataAccessException) {
            throw UserNotFoundException("UserNotFoundException")
        }
    }

    @PostMapping("/logout")
    internal fun logout(@RequestHeader Authorization: String,
                        request: HttpServletRequest) {
        AuthenticationService.deleteToken(UUID.fromString(Authorization.split(" ")[1]))
        request.session.invalidate()
    }
}

internal data class AuthenticationResponse
(
    val token: UUID,
    val username: String,
    val userId: UUID,
    val validUntil: LocalDateTime
)

@ResponseStatus(HttpStatus.NOT_FOUND)
internal class UserNotFoundException(message: String?) : RuntimeException(message)