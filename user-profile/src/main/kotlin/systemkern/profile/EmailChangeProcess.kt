package systemkern.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime.now
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@RestController
internal class EmailChangeController(
    val emailChangeService: EmailChangeService,
    val userProfileService: UserProfileService,
    @Autowired
    val mailUtility: MailUtility,
    val timeUntilTokenIsValid :Long = 6
) {
    @PostMapping("/email-change")
    internal fun saveRequest(@RequestBody emailChangeRequest: EmailChangeRequest
    ): EmailChangeResponse {

        val now = now()
        val emailChangeRequestId = UUID.randomUUID()
        val userProfile = userProfileService.findById(emailChangeRequest.userProfileId).get()
        sendEmails(userProfile.email,emailChangeRequestId)
        sendEmails(emailChangeRequest.newEmailAddress,emailChangeRequestId)
        val validUntil = now.plusHours(timeUntilTokenIsValid)
        emailChangeService.save(EmailChangeEntity(
            emailChangeRequestId,
            now,
            validUntil,
            now,
            emailChangeRequest.newEmailAddress,
            userProfile))
        return EmailChangeResponse(emailChangeRequestId,validUntil )
    }

    internal fun sendEmails(emailAddress: String,id: UUID){
        mailUtility.createEmailMessage(emailAddress, id, "/email-change",
            "Verify new email for launcher")
        mailUtility.sendMessage()
    }

    @PostMapping("/email-change/{id}")
    internal fun confirmEmail(@PathVariable("id") emailChangeRequestId: UUID
    ): EmailChangeEntity {
        val emailChangeEntity = emailChangeService.findById(emailChangeRequestId).get()
        val now = now()
        if(emailChangeEntity.validUntil < now)
            throw EmailTokenExpired()
        emailChangeEntity.completionDate = now
        emailChangeEntity.userProfile.email = emailChangeEntity.newEmailAddress
        return emailChangeService.save(emailChangeEntity)
    }
}

@Service
internal class EmailChangeService(private val emailChangeRepository: EmailChangeRepository) {
    internal fun save(emailChangeEntity: EmailChangeEntity) = emailChangeRepository.save(emailChangeEntity)
    internal fun findById(id: UUID) = emailChangeRepository.findById(id)
}

@Repository
internal interface EmailChangeRepository : CrudRepository<EmailChangeEntity, UUID>

internal data class EmailChangeRequest(val newEmailAddress: String, val userProfileId: UUID)

@Entity
internal data class EmailChangeEntity(
    @Id
    @Column(name = "id_email_change_entity")
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    var completionDate: LocalDateTime,
    val newEmailAddress: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_profile", nullable = false)
    val userProfile: UserProfile
)

internal data class EmailChangeResponse(val emailChangeReqId: UUID, val validUntil: LocalDateTime)

@ResponseStatus(NOT_ACCEPTABLE)
internal class EmailTokenExpired: RuntimeException()
