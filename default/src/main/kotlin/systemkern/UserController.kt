package systemkern

import org.springframework.web.bind.annotation.*
import sun.security.util.Password

@RestController
@RequestMapping("/users")
class UserController(val usersRepository: UsersRepository) {

    @GetMapping("/all")
    fun all(): MutableIterable<User> = this.usersRepository.findAll()

    @GetMapping("/{name}")
    fun byName(@PathVariable(value = "name") name: String): List<User> {
        val usersByname = this.usersRepository.findByName(name)
        return usersByname
    }
    @GetMapping("/{name}/{password}")
    fun byNameAndPassword(@PathVariable(value = "name") name: String,
                          @PathVariable(value = "password") password: String): List<User>
    {
        val usersByname = this.usersRepository.findByNameAndPassword(name,password)
        return usersByname
    }
    @PostMapping("/createUser")
    fun createUser(@RequestBody InsertUserRequest:InsertUserRequest)
    {
        val user = User(InsertUserRequest.name, InsertUserRequest.password)
        this.usersRepository.save(user)
    }
    @PostMapping("/loginUser")
    fun loginUser(@RequestBody loginRequest: LoginRequest):List<User>
    {
        return this.usersRepository.findByNameAndPassword(loginRequest.name, loginRequest.password)
    }
}