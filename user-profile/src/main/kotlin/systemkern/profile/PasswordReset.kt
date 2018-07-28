package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime.now
import java.time.LocalDateTime
import java.util.UUID.randomUUID
import java.util.UUID
import javax.persistence.*


@RestController
internal class PasswordResetController(
    val passwordResetService: PasswordResetService,
    val userProfileService: UserProfileService,
    val authenticationService: AuthenticationService,
    val mailUtility: MailUtility,
    val timeUntilTokenExpiresInHours: Long = 6
) {

    @PostMapping("/password-reset")
    internal fun requestPasswordReset(
        @RequestHeader("username") username: String
    ): RequestPasswordResetEntity {
        val localTime = now()
        val userProfile = userProfileService.findByUsername(username)
        val tokenId = randomUUID()
        val passwordResetEntity =
            PasswordResetEntity(
                tokenId,
                localTime,
                localTime.plusHours(timeUntilTokenExpiresInHours),
                localTime,
                userProfile
            )

        passwordResetService.save(passwordResetEntity)
        mailUtility.createEmailMessage(
            userProfile.email,
            tokenId,
            "/password-reset/",
            "Reset password request")
        mailUtility.sendMessage()

        return RequestPasswordResetEntity(
            tokenId,
            localTime,
            localTime.plusHours(timeUntilTokenExpiresInHours),
            localTime
        )
    }

    @PostMapping("/password-reset/{id}")
    internal fun confirmPasswordReset(
       @PathVariable("id") passwordResetId: UUID,
       @RequestBody newPasswordResetBody: NewPasswordResetBody
    ) : AuthenticationResponse {
        val passwordResetEntity = passwordResetService.findById(passwordResetId).get()
        val last = passwordResetEntity.userProfile.emailVerificationList.last()

        val completionDate = now()
        if(completionDate <= passwordResetEntity.validUntil
             && last.creationDate < last.completionDate //This line checks if userProfile is verified
        ) {
            passwordResetEntity.completionDate = completionDate
            passwordResetEntity.userProfile.password = newPasswordResetBody.password
            passwordResetService.save(passwordResetEntity)
            userProfileService.save(passwordResetEntity.userProfile)

            return authenticationService.authProcessPasswordReset(passwordResetEntity,completionDate)
        }
        throw ExpiredTokenException()
    }
}

internal data class NewPasswordResetBody(val password: String)

@Service
internal class PasswordResetService(private val repo: PasswordResetRepository) {

    internal fun save(passwordResetEntity : PasswordResetEntity) {
        repo.save(passwordResetEntity)
    }

    internal fun findById(id: UUID) =
        repo.findById(id)
}

internal interface PasswordResetRepository : CrudRepository<PasswordResetEntity, UUID>

@Entity
internal data class PasswordResetEntity(
    @Id
    val idPasswordResetEntity: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    var completionDate: LocalDateTime,
    @ManyToOne
    val userProfile: UserProfile
)

internal data class RequestPasswordResetEntity(
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val completionDate: LocalDateTime
)
