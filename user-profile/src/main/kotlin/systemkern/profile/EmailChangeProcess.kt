package systemkern.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@RestController
internal class EmailChangeController(
    val emailChangeService: EmailChangeService,
    val userProfileService: UserProfileService,
    @Autowired
    val mailUtility: MailUtility
) {
    @PostMapping("/email-change")
    internal fun saveRequest(@RequestBody emailChangeRequest: EmailChangeRequest)
        : EmailChangeEntity {
        val now = LocalDateTime.now()
        val emailChangeRequestId = UUID.randomUUID()
        val userProfile = userProfileService.findById(emailChangeRequest.userProfileId).get()
        mailUtility.createEmailMessage(userProfile.email, emailChangeRequestId, "/email-change",
            "Verify old email for launcher")
        return emailChangeService.save(EmailChangeEntity(
            emailChangeRequestId,
            now,
            now.plusHours(6),
            now,
            emailChangeRequest.newEmailAddress,
            userProfile))
    }
}

@Service
internal class EmailChangeService(val emailChangeRepository: EmailChangeRepository) {
    internal fun save(emailChangeEntity: EmailChangeEntity) = emailChangeRepository.save(emailChangeEntity)
}

@Repository
internal interface EmailChangeRepository : CrudRepository<EmailChangeEntity, UUID>

internal data class EmailChangeRequest(val newEmailAddress: String, val userProfileId: UUID)

@Entity
internal data class EmailChangeEntity(
    @Id
    @Column(name = "id_email_change_entity")
    @JsonIgnore
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val completionDate: LocalDateTime,
    val newEmailAddress: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_profile", nullable = false)
    val userProfile: UserProfile
)
