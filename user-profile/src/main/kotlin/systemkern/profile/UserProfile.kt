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
    var name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String, /*This attribute is var because of how repository event handler works*/
    var username: String,
    var email: String,

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_profile")
    val emailVerificationList: List<EmailVerification> = ArrayList()
)
