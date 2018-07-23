package systemkern.profile

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.OneToMany
import javax.persistence.FetchType.LAZY
import javax.persistence.JoinColumn

import java.util.UUID.randomUUID

@Entity
@EntityListeners(UserProfileEntityListener::class)
internal data class UserProfile(
    @Id
    val id: UUID = randomUUID(),
    var name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String, /*This attribute is var because of how repository event handler works*/
    var username: String,
    var email: String,

    @OneToMany(fetch = LAZY)
    @JoinColumn(name = "id_user_profile")
    val emailChangeList: List<EmailChangeEntity> = ArrayList(),

    @OneToMany(fetch = LAZY)
    @JoinColumn(name = "id_user_profile")
    val emailVerificationList: List<EmailVerification> = ArrayList()
)