package systemkern

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usersLogin")
class LoginController(val usersRepository: UsersRepository)
{
    @PostMapping
    fun loginUser(@RequestBody loginRequest: LoginRequest):List<User>
    {
        return this.usersRepository.findByNameAndPassword(loginRequest.name, loginRequest.password)
    }
}