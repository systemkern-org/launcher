package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@RestController
internal class EmailChangeController(val emailChangeService: EmailChangeService){
    @PostMapping("/email-change")
    internal fun saveRequest(@RequestBody emailChangeRequest: EmailChangeRequest){
        val now = LocalDateTime.now()
        val emailChangeEntity = EmailChangeEntity(
            UUID.randomUUID(),
            now,
            now.plusHours(6),
            now,
            emailChangeRequest.newEmailAddress,
            emailChangeService.findUserProfileById(emailChangeRequest.userProfileId).get())

    }
}

@Service
internal class EmailChangeService(val emailChangeRepository: EmailChangeRepository,
                                  val userProfileRepository: UserProfileRepository){

    internal fun save(emailChangeEntity: EmailChangeEntity)
        = emailChangeRepository.save(emailChangeEntity)

    internal fun findUserProfileById(id: UUID)
        = userProfileRepository.findById(id)
}

@Repository
internal interface EmailChangeRepository: CrudRepository<EmailChangeEntity,UUID>

internal data class EmailChangeRequest(val newEmailAddress: String,val userProfileId: UUID)

@Entity
internal data class EmailChangeEntity(
    @Id
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val completionDate: LocalDateTime,
    val newEmailAddress: String,
    @ManyToOne
    @JoinColumn
    val userProfile: UserProfile
)
