package systemkern.profile

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id

@Entity
data class UserProfile(
    @Id
    @GeneratedValue(strategy = AUTO)
    val id: UUID = randomUUID(),
    val name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String /*This attribute is var because of how repository event handler works*/
)
