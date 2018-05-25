package systemkern.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ImportResource
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@RepositoryRestResource
internal interface UsersRepository : CrudRepository<User, Long>

@ImportResource("classpath:spring-security.xml")
@Component
@RepositoryEventHandler(User::class)

internal class UserEventHandler(
    @Autowired
    internal val passwordEncoder: BCryptPasswordEncoder,
    internal val userRepository: UsersRepository
) {
    @HandleBeforeCreate
    fun handleUserCreate(user: User) {
        user.password = passwordEncoder.encode(user.password)
    }

}