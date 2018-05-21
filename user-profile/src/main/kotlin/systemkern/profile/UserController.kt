package systemkern.profile

import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

@Component
@RestController
@RequestMapping("/users")
internal class UserController(val userServ: UserService) {
    @GetMapping("{id}")
    fun byId(@PathVariable(value = "id") id: Long): UserDTO {
        return this.toDTO(this.userServ.findbyId(id))
    }

    @PostMapping
    fun create(@RequestBody userDTO: UserDTO) {
        this.userServ.create(this.toEntity(userDTO))
    }

    @PutMapping("{id}")
    fun update(@PathVariable(value = "id") id: Long, @RequestBody userDTO: UserDTO) {
        this.userServ.update(id, this.toEntity(userDTO))
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable(value = "id") id: Long) {
        this.userServ.delete(id)
    }

    fun toDTO(user: User): UserDTO = UserDTO(name = user.name, password = user.password)
    fun toEntity(userDTO: UserDTO) = User(name = userDTO.name, password = userDTO.password)
}
data class UserDTO(
    var name: String,
    var password: String
)
