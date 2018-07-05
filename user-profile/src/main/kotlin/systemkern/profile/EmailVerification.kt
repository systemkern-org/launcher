package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@RestController
internal class EmailVerificationController(
    val emailVerificationService: EmailVerificationService,
    val authenticationService: AuthenticationService) {

    @PostMapping("/verify-email/{id}")
    fun verifyUserByToken(@PathVariable("id") tokenId: String,
                          @RequestHeader password: String,
                          auth: Authentication): AuthenticationResponse {

        val emailVerification = emailVerificationService.findById(UUID.fromString(tokenId)).get()
        val completionDate = LocalDateTime.now()
        if (LocalDateTime.now() <= emailVerification.validUntil) {
            emailVerification.completionDate = completionDate
            emailVerificationService.save(emailVerification)
            return authenticationService.authenticationProcess(auth, password)
        }
        throw ExpiredTokenException("Token has expired")
    }
}

@Service
internal class EmailVerificationService(val repo: EmailVerificationRepository) {
    internal fun findById(id: UUID) =
        repo.findById(id)

    internal fun save(emailVerification: EmailVerification) =
        repo.save(emailVerification)
}

internal interface EmailVerificationRepository : CrudRepository<EmailVerification, UUID>

@Entity
internal data class EmailVerification(
    @Id
    @Column(name = "id_email_verification")
    val id_email_verification: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    var completionDate: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_profile", nullable = false)
    val userProfile: UserProfile
)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class ExpiredTokenException(message: String?) : RuntimeException()