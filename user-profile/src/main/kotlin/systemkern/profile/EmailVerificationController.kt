package systemkern.profile

import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity


@RestController
internal class EmailVerificationController

@Entity
internal data class EmailVerification(
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val completionDate: LocalDateTime,
    val userProfileId: UUID
)
