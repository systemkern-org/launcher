package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Service
internal class UserService(
    private val repo: UsersRepository
) {


    fun findbyId(id: Long): User {
        return this.repo.findById(id).get()
    }

    fun create(user: User) {
        this.repo.save(user)
    }

    fun update(id: Long, user: User) {
        var userToUpdate = this.repo.findById(id).get()
        //This was necessary because val doesnt let to change value of name or password
        userToUpdate.copy(name = user.name, password = user.password)

        this.repo.save(userToUpdate)
    }

    fun delete(id: Long) {
        this.repo.delete(this.repo.findById(id).get())
    }
}

@Repository
internal interface UsersRepository : CrudRepository<User, Long> {
    fun findByNameAndPassword(name: String, password: String): List<User>
}