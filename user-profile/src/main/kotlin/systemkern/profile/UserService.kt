package systemkern.profile

import org.springframework.stereotype.Service

@Service
internal class UserService(private val repo: UsersRepository) {


    fun findbyId(id: Long): User {
        return this.repo.findById(id).get()
    }

    fun create(user: User) {
        this.repo.save(user)
    }

    fun update(id: Long, user: User) {
        var userToUpdate = this.repo.findById(id).get()
        userToUpdate.name = user.name
        userToUpdate.password = user.password

        this.repo.save(userToUpdate)
    }

    fun delete(id: Long) {
        this.repo.delete(this.repo.findById(id).get())
    }


}