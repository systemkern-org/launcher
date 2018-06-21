package systemkern.profile

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
import java.time.Duration
import java.util.*
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

@RepositoryRestResource(path = "user-profiles")
internal interface UserProfileRepository : CrudRepository<UserProfile, UUID> {
    fun findByUsername(username: String): UserProfile
}

@Component
internal class UserProfileEntiyListener(
    internal val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {

    @PrePersist
    internal fun handleUserCreate(userProfile: UserProfile) {
        userProfile.password = passwordEncoder.encode(userProfile.password)
    }

    @PreUpdate
    internal fun handleUserUpdate(userProfile: UserProfile) {
        userProfile.password = passwordEncoder.encode(userProfile.password)
    }
}


@Configuration
internal class RepositoryRestConfig : RepositoryRestConfigurer {
    override fun configureConversionService(conversionService: ConfigurableConversionService?) {}

    override fun configureExceptionHandlerExceptionResolver(exceptionResolver: ExceptionHandlerExceptionResolver?) {}

    override fun configureHttpMessageConverters(messageConverters: MutableList<HttpMessageConverter<*>>?) {}

    override fun configureJacksonObjectMapper(objectMapper: ObjectMapper?) {}

    override fun configureRepositoryRestConfiguration(
        config: RepositoryRestConfiguration) {
        config.exposeIdsFor(UserProfile::class.java)
    }

    override fun configureValidatingRepositoryEventListener(validatingListener: ValidatingRepositoryEventListener?) {}
}


@Configuration
@ConfigurationProperties("user-profile")
internal class UserProfileConfiguration {
    var bcryptEncodeRounds: Int = 10
    var sessionTimeOut: Duration = Duration.ofMinutes(30)

    @Bean internal fun bcryptPasswordEncoderBean() =
        BCryptPasswordEncoder(bcryptEncodeRounds)

    @Bean internal fun sessTimeOutBean() =
        sessionTimeOut
}

@Component
internal class SessionListener(val sessionTimeOut: Duration) : HttpSessionListener {

    override fun sessionDestroyed(p0: HttpSessionEvent?) {}

    override fun sessionCreated(event: HttpSessionEvent) {
        event.session.maxInactiveInterval = 30 * 60
    }
}
