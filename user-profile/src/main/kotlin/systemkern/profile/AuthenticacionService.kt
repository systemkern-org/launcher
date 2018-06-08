package systemkern.profile

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

@Service
internal class AuthenticationService {

    companion object {
        val tokens: HashMap<UUID, AuthenticationResponse> = HashMap()

        internal fun isValidToken(token: UUID): Boolean {
            if (tokens.containsKey(token)) {

                val authResp: AuthenticationResponse = tokens[token] as AuthenticationResponse
                if (authResp.validUntil.isEqual(LocalDateTime.now())
                    || authResp.validUntil.isAfter(LocalDateTime.now())) {
                    return true

                } else {
                    tokens.remove(token)
                }
            }
            return false
        }

        internal fun saveToken(token: UUID, auth: AuthenticationResponse) {
            tokens[token] = auth
        }

    }
}
