package systemkern.profile

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
class AuthenticationController(val repo: UserRepository) {

    @PostMapping("/login")
    fun login(@RequestBody loginData: LoginData): Boolean {
        val passwordEncoder = BCryptPasswordEncoder()
        return passwordEncoder.matches(loginData.password,
            repo.findByUsername(loginData.username).password)
    }

}

class LoginData(
    val username: String,
    val password: String
)

data class AuthenticationResponse(
    val token: UUID,
    val username: String,
    val userId: UUID,
    val validUntil: LocalDateTime
)