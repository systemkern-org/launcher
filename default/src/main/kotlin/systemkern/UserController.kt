package systemkern

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
internal class UserController(val userServ: UserService)
{
    @GetMapping("{id}")
    fun byId(@PathVariable(value = "id") id: Long): User
    {
        return this.userServ.findbyId(id)
    }
    @PostMapping
    fun create(@RequestBody InsertUserRequest:InsertUserRequest)
    {
        this.userServ.create(InsertUserRequest)
    }
    @PutMapping("{id}")
    fun update(@PathVariable(value = "id") id: Long,
               @RequestBody updateUserRequest: updateUserRequest)
    {
        this.userServ.update(id,updateUserRequest)
    }
    @DeleteMapping("{id}")
    fun delete(@PathVariable(value = "id") id: Long)
    {
        this.userServ.delete(id)
    }
}