package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Service
internal class UserService(
    private val repo: UsersRepository
) {


    fun findbyId(id: Long): User =
        this.repo.findById(id).get()

    fun findByNameAndPassword(name: String, password: String): User =
        this.repo.findByNameAndPassword(name, password).last()

    fun create(user: User): User =
        this.repo.save(user)

    fun update(id: Long, user: User): User {
        val userToUpdate = this.repo.findById(id).get()
        return this.repo.save(
            userToUpdate.copy(
                name = user.name,
                password = user.password
            ))
    }

    fun delete(id: Long) {
        this.repo.delete(this.repo.findById(id).get())
    }

}

@Repository
internal interface UsersRepository : CrudRepository<User, Long> {
    fun findByNameAndPassword(name: String, password: String): List<User>
}