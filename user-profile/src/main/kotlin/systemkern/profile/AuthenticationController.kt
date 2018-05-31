package systemkern.profile

import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
class AuthenticationController(val repo: UserRepository) {

    @PostMapping("/login")
    fun login(@RequestBody loginData: LoginData): AuthenticationResponse {
        val passwordEncoder = BCryptPasswordEncoder()
        val user = repo.findByUsername(loginData.username)

        if(passwordEncoder.matches(loginData.password, user.password))
        {
            return AuthenticationResponse(
                token = UUID.randomUUID(),
                username = user.username,
                userId = user.id,
                validUntil = LocalDateTime.MAX
            )

        }else
        {
            throw UserNotFoundException("UserNotFoundException")
        }
    }

}

data class LoginData(
    val username: String,
    val password: String
)

class AuthenticationResponse
(
    val token: UUID,
    val username: String,
    val userId: UUID,
    val validUntil: LocalDateTime
)

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException(message: String?) : RuntimeException(message)