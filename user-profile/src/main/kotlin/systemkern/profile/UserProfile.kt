package systemkern.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.UUID
import java.util.UUID.randomUUID
import javax.persistence.*

@Entity
@EntityListeners(UserProfileEntityListener::class)
internal data class UserProfile(
    @Id
    @JsonIgnore
    val id: UUID = randomUUID(),
    var name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String, /*This attribute is var because of how repository event handler works*/
    var username: String,
    var email: String,

    @OneToMany
    @JoinColumn(name = "user_profile_id")
    @JsonIgnore
    val emailVerificationList: List<EmailVerification> = ArrayList(),

    @OneToMany
    @JoinColumn(name = "user_profile_id")
    @JsonIgnore
    val emailChangeList: List<EmailChangeEntity> = ArrayList()
)
