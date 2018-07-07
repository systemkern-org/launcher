package systemkern.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.security.core.Authentication
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
    @Autowired
    val mailUtility: MailUtility,
    val timeUntilTokenExpiresInHours: Long = 6
) {
    @PostMapping("/password-reset")
    internal fun requestPasswordReset(@RequestHeader("username") username: String): RequestPasswordResetEntity {
        val localTime = now()
        val userProfile = userProfileService.findByUsername(username)
        val tokenId = randomUUID()
        val passwordResetEntity =
            PasswordResetEntity(tokenId,
                userProfile,
                localTime,
                localTime.plusHours(timeUntilTokenExpiresInHours),
                localTime)

        passwordResetService.save(passwordResetEntity)
        mailUtility.createEmailMessage(userProfile.email, tokenId, "/password-reset/"
            , "Reset password request")
        mailUtility.sendMessage()

        return RequestPasswordResetEntity(tokenId,
            localTime,
            localTime.plusHours(timeUntilTokenExpiresInHours),
            localTime)
    }

    @PostMapping("/password-reset/{id}")
    internal fun confirmPasswordReset(@PathVariable("id") id: UUID,
                                      @RequestBody newPasswordResetBody: NewPasswordResetBody,
                                      auth: Authentication
    ) {
        val passwordResetEntity = passwordResetService.findById(id).get()
        val userProfile = passwordResetEntity.userProfile
        val completionDate = LocalDateTime.now()
        if (completionDate <= passwordResetEntity.validUntil) {
            passwordResetEntity.completionDate = completionDate
            userProfile.password = newPasswordResetBody.password
            return passwordResetService.save(passwordResetEntity)
        }
        throw ExpiredTokenException("Token has expired")
    }
}

internal data class NewPasswordResetBody(val password: String)

@Service
internal class PasswordResetService(private val repo: PasswordResetRepository) {

    internal fun save(passwordResetEntity: PasswordResetEntity) {
        repo.save(passwordResetEntity)
    }

    internal fun findById(id: UUID) =
        repo.findById(id)
}

internal interface PasswordResetRepository : CrudRepository<PasswordResetEntity, UUID>

@Entity
internal data class PasswordResetEntity(
    @Id
    @Column(name = "id_password_reset_entity")
    val id_password_reset_entity: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_profile", nullable = false)
    val userProfile: UserProfile,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    var completionDate: LocalDateTime)

internal data class RequestPasswordResetEntity(
    val id: UUID,
    val creationDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val completionDate: LocalDateTime)
