package systemkern.profile

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
internal data class User(
    val name: String,
    val password: String,
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0)