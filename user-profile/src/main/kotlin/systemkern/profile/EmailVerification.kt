package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime.now
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Id

@RestController
internal class EmailVerificationController(
    val emailVerificationService: EmailVerificationService,
    val authenticationService: AuthenticationService) {

    @PostMapping("/verify-email/{id}")
    fun verifyUserByToken(@PathVariable("id") tokenId: UUID
    ): AuthenticationResponse {
        val emailVerification = emailVerificationService.findById(tokenId).get()
        val completionDate = now()
        if (completionDate <= emailVerification.validUntil) {
            emailVerification.completionDate = completionDate
            emailVerificationService.save(emailVerification)
            return authenticationService.authProcessEmailVerification(tokenId)
        }
        throw ExpiredTokenException()
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
    val idEmailVerification: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    var completionDate: LocalDateTime,
    @ManyToOne
    val userProfile: UserProfile
)

@ResponseStatus(UNAUTHORIZED)
internal class ExpiredTokenException : RuntimeException()