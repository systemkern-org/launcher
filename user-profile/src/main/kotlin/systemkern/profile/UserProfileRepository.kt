package systemkern.profile

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
import java.time.LocalDateTime
import java.time.Duration
import java.util.*
import javax.persistence.*
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener
import java.util.regex.Pattern
import java.util.zip.DataFormatException

@RestController
internal class UserProfileController(val userProfileService: UserProfileService,
                                     val emailVerificationService: EmailVerificationService,
                                     @Autowired
                                     val mailUtility: MailUtility
) {
    @PostMapping("user-profiles")
    private fun saveUser(@RequestBody requestBody: UserProfile): SaveUserProfileResponse {
        userProfileService.save(requestBody)
        val localDateTime = LocalDateTime.now()
        val tokenId = UUID.randomUUID()
        val emailVerificationEntity = EmailVerification(
            tokenId,
            localDateTime,
            localDateTime.plusHours(6),
            localDateTime,
            requestBody
        )
        emailVerificationService.save(emailVerificationEntity)
        mailUtility.createEmailMessage(requestBody.email, tokenId, "/verify-email/",
            "Verify launcher account")
        mailUtility.sendMessage()
        return SaveUserProfileResponse(mailUtility.urlToVerify)
    }

    private data class SaveUserProfileResponse(var url: String)
}

@RepositoryRestResource(path = "user-profiles")
internal interface UserProfileRepository : CrudRepository<UserProfile, UUID> {
    fun findByUsername(username: String): UserProfile
}

@Component
internal class UserProfileEntityListener(
    internal val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {
    private val emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*" +
        "@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

    fun validateEmail(hex: String)
        = emailPattern.matcher(hex).matches()

    private fun executeValidation(emailToVal: String){
        if (!validateEmail(emailToVal))
            throw BadEmailException("Email address is invalid")
    }

    @PrePersist
    internal fun handleUserCreate(userProfile: UserProfile) {
        executeValidation(userProfile.email)
        userProfile.password = passwordEncoder.encode(userProfile.password)
    }

    @PreUpdate
    internal fun handleUserUpdate(userProfile: UserProfile) {
        executeValidation(userProfile.email)
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

    override fun sessionDestroyed(p0: HttpSessionEvent?) {

    }

    override fun sessionCreated(event: HttpSessionEvent) {
        event.session.maxInactiveInterval =  sessionTimeOut.toMinutes().toInt()
    }
}

@ResponseStatus(NOT_ACCEPTABLE)
internal class BadEmailException(message: String?) : DataFormatException(message)