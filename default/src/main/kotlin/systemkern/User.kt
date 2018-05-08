package systemkern

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User(val name: String, val password: String)
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    /**
     * We need a default constructor for this class because it is serialized/deserialzied
     * by the REST controller
     */
    constructor(): this("", "")

}