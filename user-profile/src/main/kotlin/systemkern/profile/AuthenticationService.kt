package systemkern.profile

import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

@Service
internal class AuthenticationService(
    val repo: UserProfileRepository
) {
    private var inactiveInterval:Long = 0
    private var maxInactiveIntervalMilis = 0
    val tokens: HashMap<UUID, AuthenticationResponse> = HashMap()

    internal fun findByUsername(username: String)= repo.findByUsername(username)
    internal fun isValidToken(token: UUID, request: HttpServletRequest): Boolean {
        inactiveInterval = System.currentTimeMillis() - request.session.lastAccessedTime
        maxInactiveIntervalMilis = request.session.maxInactiveInterval * 1000
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
}
