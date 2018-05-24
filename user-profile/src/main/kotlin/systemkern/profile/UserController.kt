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
    fun byId(@PathVariable(value = "id") id: Long): User {
        val user = this.userServ.findbyId(id)
        return user
    }

    //TODO: Responder con toda la entidad
    @PostMapping
    fun create(@RequestBody userDTO: UserDTO): User =
        this.userServ.create(userDTO.toEntity(userDTO))

    //TODO: Responder con toda la entidad
    @PutMapping("{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody userDTO: UserDTO) =
        this.userServ.update(id, userDTO.toEntity(userDTO))

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
        id = -1,
        name = name,
        password = password
    )

data class UserDTO(
    val name: String,
    val password: String
)
