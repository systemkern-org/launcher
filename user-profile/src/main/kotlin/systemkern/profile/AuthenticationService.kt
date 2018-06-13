package systemkern.profile


import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import kotlin.collections.HashMap

@Service
internal class AuthenticationService {

    companion object {
        val tokens: HashMap<UUID, AuthenticationResponse> = HashMap()

        internal fun isValidToken(token: UUID, request: HttpServletRequest): Boolean {
            val sess: HttpSession = request.session
            if (tokens.containsKey(token)) {
                return System.currentTimeMillis() - sess.lastAccessedTime <= sess.maxInactiveInterval * 1000
            }
            tokens.remove(token)
            return false
        }

        internal fun saveToken(token: UUID, auth: AuthenticationResponse) {
            tokens[token] = auth
        }

        internal fun deleteToken(token: UUID) {
            tokens.remove(token)
        }

    }
}
