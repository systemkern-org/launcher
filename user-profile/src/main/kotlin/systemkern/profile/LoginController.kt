package systemkern.profile

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class LoginController(val repo: UserRepository) {
    @PostMapping("/login")
    fun login(@RequestBody loginData: LoginData): Boolean{
        val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
        return passwordEncoder.matches(loginData.password,repo.findByUsername(loginData.username).password)
    }

}

class LoginData(
    val username: String,
    val password: String
)