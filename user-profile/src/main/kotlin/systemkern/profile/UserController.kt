package systemkern.profile

import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

@Component
@RestController
@RequestMapping("/users")
internal class UserController(
    private val userServ: UserService
) {
    @GetMapping("{id}")
    fun byId(@PathVariable(value = "id") id: Long): UserDTO {
        val user = this.userServ.findbyId(id)
        return user.toDTO(user)
    }

    @PostMapping
    fun create(@RequestBody userDTO: UserDTO) {

        this.userServ.create(userDTO.toEntity(userDTO))
    }

    @PutMapping("{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody userDTO: UserDTO) {
        this.userServ.update(id, userDTO.toEntity(userDTO))
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: Long) {
        this.userServ.delete(id)
    }
}

internal fun User.toDTO(user: User) =
    UserDTO(
        name = name,
        password = password
    )

internal fun UserDTO.toEntity(userDTO: UserDTO) =
    User(
        name = name,
        password = password
    )

data class UserDTO(
    val name: String,
    val password: String
)
