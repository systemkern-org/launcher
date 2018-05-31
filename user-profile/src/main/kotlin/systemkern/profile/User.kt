package systemkern.profile

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.*
import javax.persistence.*

@Entity(name="user")
//@Table(name="user", schema = "public")

data class User(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String, /*This attribute is var because of how repository event handler works*/
    val username: String
)
