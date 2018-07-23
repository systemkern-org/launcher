package systemkern.profile

import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap
import java.time.Duration

@Service
internal class AuthenticationService(
    val emailVerificationRepository: EmailVerificationRepository,
     val emailChangeRepository: EmailChangeRepository,
     val repo: UserProfileRepository,
     val passwordEncoder: BCryptPasswordEncoder,
     val sessionTimeOut: Duration,
     val auxNumToConvertSecstoMillis:Int = 1000
) {
    val tokens: HashMap<UUID, AuthenticationResponse> = HashMap()

    internal fun findByUsername(username: String) =
        repo.findByUsername(username)

    internal fun isValidToken(token: UUID, request: HttpServletRequest): Boolean {
        val inactiveInterval = System.currentTimeMillis() - request.session.lastAccessedTime
        val maxInactiveIntervalMilis = request.session.maxInactiveInterval * auxNumToConvertSecstoMillis
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
        val emailVerification = user.emailVerificationList.last()
        if (!passwordEncoder.matches(password, user.password)
            && emailVerification.completionDate <= emailVerification.creationDate)
            throw UserNotFoundException("UserNotFoundException")
        val token: UUID = UUID.fromString(auth.credentials.toString())
        val validUntil = now().plusMinutes(sessionTimeOut.toMinutes())
        val authResp = AuthenticationResponse(
            token = token,
            username = user.username,
            userId = user.id,
            validUntil = validUntil
        )
        saveToken(token, authResp)
        return authResp
    }

    internal fun authProcessEmailVerification(verifyEmailToken: UUID): AuthenticationResponse {
        val emailVerification = emailVerificationRepository.findById(verifyEmailToken).get()
        val userProfile = emailVerification.userProfile

        return buildResponseAndSave(
            authenticationToken = UUID.randomUUID(),
            username = userProfile.username,
            userId = userProfile.id,
            validUntil = now().plusMinutes(sessionTimeOut.toMinutes()))
    }

    internal fun authProcessEmailChange(emailChangeToken: UUID): AuthenticationResponse {
        val emailChangeEntity = emailChangeRepository.findById(emailChangeToken).get()
        val userProfile = emailChangeEntity.userProfile

        return buildResponseAndSave(
            authenticationToken = UUID.randomUUID(),
            username = userProfile.username,
            userId = userProfile.id,
            validUntil = now().plusMinutes(sessionTimeOut.toMinutes()))
    }

    private fun buildResponseAndSave(
        authenticationToken: UUID,
        validUntil: LocalDateTime,
        username: String,
        userId: UUID): AuthenticationResponse {
        val authResp = AuthenticationResponse(
            token = authenticationToken,
            username = username,
            userId = userId,
            validUntil = validUntil
        )
        saveToken(authenticationToken, authResp)
        return authResp
    }
}

