package systemkern

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(val usersRepository: UsersRepository)
{
    @GetMapping("{id}")
    fun byId(@PathVariable(value = "id") id: Long): User
    {
        return this.usersRepository.findById(id).get()
    }
    @PostMapping
    fun createUser(@RequestBody InsertUserRequest:InsertUserRequest)
    {
        val user = User(InsertUserRequest.name, InsertUserRequest.password)
        this.usersRepository.save(user)
    }
}