package systemkern.profile

import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap


internal class AuthenticationService{

    companion object {
        @JvmStatic
        val tokens: HashMap<UUID, AuthenticationResponse> = HashMap()

        fun isValidToken(token: UUID):Boolean
        {
            if(tokens.containsKey(token)) {

                val authResp: AuthenticationResponse = tokens.get(token) as AuthenticationResponse
                if(authResp.validUntil.isEqual(LocalDateTime.now())
                     || authResp.validUntil.isAfter(LocalDateTime.now()))
                {
                    return true

                }else
                {
                    tokens.remove(token)
                    return false
                }
            }
            return false
        }
        fun saveToken(token: UUID,auth: AuthenticationResponse)
        {
            tokens.put(token,auth)
        }
    }
}
