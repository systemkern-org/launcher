package systemkern.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.*
import javax.persistence.Id

@Entity
internal data class User(
    @Id
    @GeneratedValue(strategy = AUTO)
    val id: Long,
    val name: String,

    @JsonProperty(access = WRITE_ONLY)
    var password: String /*This attribute is var because of how repository event handler works*/

)