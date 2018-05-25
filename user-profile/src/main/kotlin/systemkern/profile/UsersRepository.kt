package systemkern.profile

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@RepositoryRestResource
internal interface UsersRepository : CrudRepository<User,Long>

@Component
@RepositoryEventHandler(User::class)
internal class UserEventHandler(
    /*@Autowired
    internal val passwordEncoder: BCryptPasswordEncoder,*/
    internal val userRepository: UsersRepository
) {
    @HandleBeforeCreate
    fun handleUserCreate(user: User) {
        user.password = "123"//passwordEncoder.encode(user.password)
    }

}