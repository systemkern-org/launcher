package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
internal interface UsersRepositorySDR : CrudRepository<User,Long>