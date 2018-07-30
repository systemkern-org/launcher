package systemkern.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.UUID
import java.util.UUID.randomUUID
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.JoinColumn

@Entity
@EntityListeners(UserProfileEntityListener::class)
internal data class UserProfile(
    @Id
    @JsonIgnore
    val id: UUID = randomUUID(),
    var name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String, /*This attribute is var because of how repository event handler works*/
    val username: String,
    var email: String,

    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "user_profile_id")
    val emailVerificationList: List<EmailVerification> = ArrayList(),

    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "user_profile_id")
    val passwordResetList: List<PasswordResetEntity> = ArrayList(),

    @OneToMany
    @JoinColumn(name = "user_profile_id")
    @JsonIgnore
    val emailChangeList: List<EmailChangeEntity> = ArrayList()

)
