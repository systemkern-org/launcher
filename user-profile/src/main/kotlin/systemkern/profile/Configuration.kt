package systemkern.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component


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

    var bcryptEncryptionRounds: Int = 20

    @Bean fun createBCryptPasswordEncoder() =
        BCryptPasswordEncoder(bcryptEncryptionRounds)

}

@Configuration
@EnableWebSecurity
class CustomWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser("user")
            .password("password")
            .authorities("ROLE_USER")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/users", "/users/")
            .authenticated()
            .and()
            .httpBasic()
    }
}
