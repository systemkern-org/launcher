package systemkern

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository : CrudRepository<User,Long>
{
    fun findByNameAndPassword(name: String,password: String): List<User>
}