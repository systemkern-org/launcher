package systemkern

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class User(var name: String, var password: String)
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