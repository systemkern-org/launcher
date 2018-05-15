package systemkern

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository : CrudRepository<User,Long>
{
    fun findByName(name: String): List<User>
    fun findByNameAndPassword(name: String,password: String): List<User>
}