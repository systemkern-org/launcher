package systemkern.profile

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*
import javax.persistence.*
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener
import java.util.regex.Pattern
import java.util.zip.DataFormatException


@RepositoryRestResource(path = "user-profiles")
internal interface UserProfileRepository : CrudRepository<UserProfile, UUID> {
    fun findByUsername(username: String): UserProfile
}

@Component
internal class UserProfileEntityListener(

    internal val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {
    val pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*" +
        "@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

    fun validateEmail(hex: String): Boolean = pattern.matcher(hex).matches()

    @PrePersist
    internal fun handleUserCreate(userProfile: UserProfile) {
        if (!validateEmail(userProfile.email))
            throw BadEmailException("Email address is invalid")
        userProfile.password = passwordEncoder.encode(userProfile.password)
    }

    @PreUpdate
    internal fun handleUserUpdate(userProfile: UserProfile) {
        userProfile.password = passwordEncoder.encode(userProfile.password)
    }

    @PostPersist
    internal fun handleAfterCreate(userProfile: UserProfile) {

    }
}


@Configuration
internal class RepositoryRestConfig : RepositoryRestConfigurer {
    override fun configureRepositoryRestConfiguration(
        config: RepositoryRestConfiguration) {
        config.exposeIdsFor(UserProfile::class.java)
    }
}


@Configuration
@ConfigurationProperties("user-profile")
internal class UserProfileConfiguration {
    val bcryptEncodeRounds: Int = 10
    @Bean
    internal fun bcryptPasswordEncoderBean() =
        BCryptPasswordEncoder(bcryptEncodeRounds)
}

@Component
internal class SessionListener : HttpSessionListener {

    override fun sessionDestroyed(p0: HttpSessionEvent?) {

    }

    override fun sessionCreated(event: HttpSessionEvent) {
        event.session.maxInactiveInterval = Parameters.sessionTime.toInt() * 60
    }
}
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
internal class BadEmailException(message: String?) : DataFormatException(message)