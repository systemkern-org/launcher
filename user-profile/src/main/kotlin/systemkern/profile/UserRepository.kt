package systemkern.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*


@RepositoryRestResource
internal interface UserRepository : CrudRepository<User, UUID>


@Component
@RepositoryEventHandler(User::class)
internal class UserEventHandler(
    @Autowired
    internal val passwordEncoder: BCryptPasswordEncoder,
    internal val userRepository: UserRepository
) {
    @HandleBeforeCreate
    fun handleUserCreate(user: User) {
        user.password = passwordEncoder.encode(user.password)
    }
}


@Configuration
internal class RepositoryRestConfig : RepositoryRestConfigurer {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(User::class.java)
    }
}


@Configuration
@ConfigurationProperties("user-profile")
internal class BCryptPasswordEncoderConfiguration {
    var bcryptEncodeRounds: Long = 10

    @Bean fun createBeanFoo() =
        BCryptPasswordEncoder(10)

}
