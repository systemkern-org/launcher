package systemkern.profile

import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap
import java.time.Duration
import java.time.LocalDateTime

@Service
internal class AuthenticationService(
    val userProfileRepository: UserProfileRepository,
    val emailVerificationRepository: EmailVerificationRepository,
    val emailChangeRepository: EmailChangeRepository,
    val passwordEncoder: BCryptPasswordEncoder,
    val sessionTimeOut: Duration,
    val auxNumToConvertSecstoMillis: Int = 1000
) {

    val tokens : HashMap<UUID, AuthenticationResponse> = HashMap()
    internal fun findByUsername(username : String) =
        userProfileRepository.findByUsername(username)

    internal fun isValidToken(token : UUID, request : HttpServletRequest) : Boolean {
         val inactiveInterval = System.currentTimeMillis() - request.session.lastAccessedTime
         val maxInactiveIntervalMilis = request.session.maxInactiveInterval * auxNumToConvertSecstoMillis
         if (tokens.containsKey(token)) {
             return inactiveInterval <= maxInactiveIntervalMilis
         }
         return false
    }

    internal fun saveToken(token : UUID, auth : AuthenticationResponse) {
         tokens[token] = auth

    }

    internal fun deleteToken(token : UUID) {
        tokens.remove(token)
    }

    @Throws(UserNotFoundException::class)
    internal fun authenticationProcess(
        auth : Authentication,
        password : String
    ) : AuthenticationResponse {
        val user = findByUsername(auth.principal.toString())
        val emailVerification = user.emailVerificationList.last()
        if (!passwordEncoder.matches(password, user.password)
            && emailVerification.completionDate <= emailVerification.creationDate)
            throw UserNotFoundException("UserNotFoundException")

        return buildResponseAndSave(
            authenticationToken = UUID.fromString(auth.credentials.toString()),
            username = user.username,
            userId = user.id,
            validUntil = now().plusMinutes(sessionTimeOut.toMinutes()))
    }

    internal fun authProcessEmailVerification(verifyEmailToken : UUID) : AuthenticationResponse {
        val emailVerification = emailVerificationRepository.findById(verifyEmailToken).get()
        val userProfile = emailVerification.userProfile

        return buildResponseAndSave(
            authenticationToken = UUID.randomUUID(),
            username = userProfile.username,
            userId = userProfile.id,
            validUntil = now().plusMinutes(sessionTimeOut.toMinutes()))
    }

    internal fun authProcessEmailChange(emailChangeToken: UUID) : AuthenticationResponse {
        val emailChangeEntity = emailChangeRepository.findById(emailChangeToken).get()
        val userProfile = emailChangeEntity.userProfile

        return buildResponseAndSave(
            authenticationToken = UUID.randomUUID(),
            username = userProfile.username,
            userId = userProfile.id,
            validUntil = now().plusMinutes(sessionTimeOut.toMinutes()))
    }

        internal fun authProcessPasswordReset(
        passwordResetEntity : PasswordResetEntity,
        completionDate : LocalDateTime
    ) : AuthenticationResponse {
         val userProfile = passwordResetEntity.userProfile
         return buildResponseAndSave(
             authenticationToken = UUID.randomUUID(),
             username = userProfile.username,
             userId = userProfile.id,
             validUntil = now().plusMinutes(sessionTimeOut.toMinutes()))
    }

    private fun buildResponseAndSave(
        authenticationToken : UUID,
        validUntil : LocalDateTime,
        username : String,
        userId : UUID
    ) : AuthenticationResponse {
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

