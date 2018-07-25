package systemkern.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.*
import javax.persistence.*
import java.util.UUID.randomUUID

@Entity
@EntityListeners(UserProfileEntityListener::class)
internal data class UserProfile(
    @Id
    @JsonIgnore
    val id: UUID = randomUUID(),
    val name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String, /*This attribute is var because of how repository event handler works*/
    val username: String,
    val email: String,

    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "user_profile_id")
    val emailVerificationList: List<EmailVerification> = ArrayList(),

    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "user_profile_id")
    val passwordResetList: List<PasswordResetEntity> = ArrayList()
)
