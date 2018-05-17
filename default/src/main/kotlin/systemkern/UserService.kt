package systemkern

import org.springframework.stereotype.Service

@Service
internal class UserService(private val repo: UsersRepository)
{
    fun findbyId(id: Long): User
    {
       return this.repo.findById(id).get()
    }
    fun create(InsertUserRequest:InsertUserRequest)
    {
        val user = User(InsertUserRequest.name, InsertUserRequest.password)
        this.repo.save(user)
    }
    fun update(id:Long,updateUserRequest: updateUserRequest)
    {
        var userToUpdate = this.repo.findById(id).get()
        userToUpdate.name = updateUserRequest.name
        userToUpdate.password = updateUserRequest.password

        this.repo.save(userToUpdate)
    }
    fun delete(id: Long)
    {
        var userTodelete = this.repo.findById(id).get()
        this.repo.delete(userTodelete)
    }
}