package systemkern.profile

import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
internal class AuthenticationController(val repo: UserProfileRepository) {

    @PostMapping("/login")
    fun login(@RequestHeader password: String,
              auth: Authentication): AuthenticationResponse {

        val passwordEncoder = BCryptPasswordEncoder()
        val user = repo.findByUsername(auth.principal.toString())

        if (!passwordEncoder.matches(password, user.password))
            throw UserNotFoundException("UserNotFoundException")

        val token: UUID = UUID.fromString(auth.credentials.toString())
        val authResp = AuthenticationResponse(
            token = token,
            username = user.username,
            userId = user.id,
            validUntil = LocalDateTime.now().plusMinutes(1)
        )
        AuthenticationService.saveToken(token,authResp)
        return authResp
    }

}

data class LoginData(
    val username: String,
    val password: String
)

data class AuthenticationResponse
(
    val token: UUID,
    val username: String,
    val userId: UUID,
    val validUntil: LocalDateTime
)

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException(message: String?) : RuntimeException(message)