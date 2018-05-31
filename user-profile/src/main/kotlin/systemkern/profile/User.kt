package systemkern.profile

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import org.springframework.web.bind.annotation.Mapping
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity(name="user")
//@Table(name="user", schema = "public")

data class User(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name")
    val name: String,

    @Column(name = "password")
    @JsonProperty(access = WRITE_ONLY)
    var password: String, /*This attribute is var because of how repository event handler works*/

    @Column(name = "username")
    val username: String
)
