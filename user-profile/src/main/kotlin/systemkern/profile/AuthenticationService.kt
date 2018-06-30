package systemkern.profile

import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

@Service
internal class AuthenticationService(val repo: UserProfileRepository,
                                     val passwordEncoder: BCryptPasswordEncoder) {
    val tokens: HashMap<UUID, AuthenticationResponse> = HashMap()

    internal fun findByUsername(username: String) =
        repo.findByUsername(username)

    internal fun isValidToken(token: UUID, request: HttpServletRequest): Boolean {
        val inactiveInterval = System.currentTimeMillis() - request.session.lastAccessedTime
        val maxInactiveIntervalMilis = request.session.maxInactiveInterval * 1000
        if (tokens.containsKey(token)) {
            return inactiveInterval <= maxInactiveIntervalMilis
        }
        return false
    }

    internal fun saveToken(token: UUID, auth: AuthenticationResponse) {
        tokens[token] = auth
    }

    internal fun deleteToken(token: UUID) {
        tokens.remove(token)
    }

    @Throws(UserNotFoundException::class)
    internal fun authenticationProcess(auth: Authentication,
                                       password: String
    ): AuthenticationResponse {

        val user = findByUsername(auth.principal.toString())
        if (!passwordEncoder.matches(password, user.password))
            throw UserNotFoundException("UserNotFoundException")
        val token: UUID = UUID.fromString(auth.credentials.toString())

        val validUntil = LocalDateTime.now().plusMinutes(Parameters.sessionTime.toLong())
        val authResp = AuthenticationResponse(
            token = token,
            username = user.username,
            userId = user.id,
            validUntil = validUntil
        )
        saveToken(token, authResp)
        return authResp
    }
}
