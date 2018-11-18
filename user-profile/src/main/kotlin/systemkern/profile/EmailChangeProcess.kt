package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.UUID
import java.util.UUID.randomUUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@RestController
internal class EmailChangeController(
    val emailChangeService: EmailChangeService,
    val userProfileService: UserProfileService,
    val mailUtility: MailUtility,
    val timeUntilTokenIsValid: Long = 6,
    val authenticationService: AuthenticationService
) {

    @PostMapping("/email-change")
    internal fun saveRequest(@RequestBody emailChangeRequest: EmailChangeRequest): EmailChangeResponse {
        val now = now()
        val userProfile = userProfileService.findById(emailChangeRequest.userProfileId).get()
        val validUntil = now.plusHours(timeUntilTokenIsValid)
        val emailChangeRequestId = emailChangeService.save(
            EmailChangeEntity(
                randomUUID(),
                now,
                validUntil,
                now,
                emailChangeRequest.newEmailAddress,
                userProfile
            )).id
        sendEmails(userProfile.email, emailChangeRequestId)
        sendEmails(emailChangeRequest.newEmailAddress, emailChangeRequestId)
        return EmailChangeResponse(emailChangeRequestId, validUntil)
    }

    internal fun sendEmails(emailAddress: String, id: UUID) {
        mailUtility.createEmailMessage(
            emailAddress,
            id,
            "/email-change",
            "Verify new email for launcher"
        )
        mailUtility.sendMessage()
    }

    @PostMapping("/email-change/{id}")
    internal fun confirmEmail(@PathVariable("id") emailChangeRequestId: UUID
    ): AuthenticationResponse {
        val emailChangeEntity = emailChangeService.findById(emailChangeRequestId)
        val last = emailChangeEntity.userProfile.emailVerificationList.last()
        emailChangeEntity.completionDate = now()
        if (emailChangeEntity.completionDate < emailChangeEntity.validUntil
            && last.creationDate < last.completionDate) {
            emailChangeEntity.userProfile.email = emailChangeEntity.newEmailAddress
            emailChangeService.save(emailChangeEntity)
            return authenticationService.authProcessEmailChange(emailChangeRequestId)
        }
        throw EmailTokenExpired()
    }
}

@Service
internal class EmailChangeService(private val emailChangeRepository: EmailChangeRepository) {

    internal fun save(emailChangeEntity: EmailChangeEntity) = emailChangeRepository.save(emailChangeEntity)

    internal fun findById(id: UUID) = emailChangeRepository.findById(id).get()
}

@Repository
internal interface EmailChangeRepository : CrudRepository<EmailChangeEntity, UUID>

internal data class EmailChangeRequest(
    val newEmailAddress: String,
    val userProfileId: UUID
)

@Entity
internal data class EmailChangeEntity(
    @Id
    val id: UUID = randomUUID(),
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    var completionDate: LocalDateTime,
    val newEmailAddress: String,
    @ManyToOne
    val userProfile: UserProfile
)

internal data class EmailChangeResponse(
    val emailChangeReqId: UUID,
    val validUntil: LocalDateTime
)

@ResponseStatus(NOT_ACCEPTABLE)
internal class EmailTokenExpired : RuntimeException()
