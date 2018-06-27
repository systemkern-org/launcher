package systemkern.profile

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@RepositoryRestResource
@Component
internal interface EmailVerificationRepository : CrudRepository<EmailVerification, UUID> {
    @PostMapping("verify-email/{tokenId}")
    @Query("SELECT * FROM public.user_profile u WHERE u.id = :tokenId", nativeQuery = true)
    fun verifyEmail(@Param("tokenId") tokenId: String): EmailVerification
}

@Entity
internal data class EmailVerification(
    @Id
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val completionDate: LocalDateTime,
    val userProfileId: UUID
)
