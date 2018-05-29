package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.annotation.Secured
import java.util.*
import org.springframework.web.bind.annotation.GetMapping


@RepositoryRestResource
internal interface UserRepository : CrudRepository<User, UUID> {

    @Override
    fun findByNameAndPassword(name: String,
                              password: String): List<User>

    @Secured("ROLE_USER")
    @GetMapping("{id}")
    override fun findById(Id: UUID): Optional<User>
}
