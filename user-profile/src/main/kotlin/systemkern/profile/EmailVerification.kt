package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id


@RestController
internal class EmailVerificationController(val serv: EmailVerificationService) {
    @GetMapping("/verify-email/{id}")
    fun verifyUserByToken(@PathVariable("id") tokenId: String): EmailVerification {
        val emailVerification = serv.findById(UUID.fromString(tokenId)).get()
        val completionDate = LocalDateTime.now()
        if (LocalDateTime.now() <= emailVerification.validUntil) {
            emailVerification.completionDate = completionDate
            serv.save(emailVerification)
        }
        return emailVerification
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
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    var completionDate: LocalDateTime,
    val userProfileId: UUID
)
