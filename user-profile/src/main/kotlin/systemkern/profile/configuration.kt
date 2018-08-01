package systemkern.profile

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder.clearContext
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse.SC_OK
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
internal class CustomWebSecurityConfigurerAdapter(
    val service: AuthenticationService
) : WebSecurityConfigurerAdapter() {
  
    val patternVerifyEmailId: String = "/verify-email/{\\d+}"
    val patternVerifyEmail: String = "/verify-email"
    val patternPasswordResetId: String = "/password-reset/{\\d+}"
    val patternPasswordReset: String = "/password-reset"
    val pattern: String = "/user-profiles"
    val pattern1: String = "/user-profiles/"
    val pattern2: String = "/user-profiles/{\\d+}"
    val patrernEmailChangeUrl = "/email-change"

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**" ,
                /*Probably not needed*/ "/swagger.json")
            .permitAll()

        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(DELETE, pattern, pattern1, pattern2, patternPasswordReset, patternVerifyEmail)
            .denyAll()

            .antMatchers(PUT, pattern2)
            .authenticated()
            .antMatchers(PUT, pattern, pattern1, patternPasswordReset)
            .denyAll()

            .antMatchers(POST, pattern)
            .permitAll()

            .antMatchers(GET, pattern2)
            .authenticated()
      
            .antMatchers(GET, pattern, pattern1, patternPasswordReset, patternVerifyEmail)
            .denyAll()

            .antMatchers(POST,patternPasswordResetId)
            .permitAll()

            .antMatchers(POST,patternVerifyEmailId)
            .permitAll()

            .and()
            .addFilterBefore(
                AuthenticationFilter(UPAuthenticationProvider(), service),
                BasicAuthenticationFilter::class.java
            )
    }
}

internal class AuthenticationFilter(
    private val authenticationProvider: UPAuthenticationProvider,
    private val service: AuthenticationService
) : GenericFilterBean() {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filter: FilterChain
    ) {
        request as HttpServletRequest
        response as HttpServletResponse
        try {
            val token: UUID = UUID.fromString(request.getHeader("Authorization").split(" ")[1])
            if (!service.isValidToken(token, request)) {
                clearContext()
            } else {
                val authRes: Authentication =
                    PreAuthenticatedAuthenticationToken(token, UUID.randomUUID())
                authRes.isAuthenticated = true
                getContext().authentication = authRes
            }
        } catch (E: IllegalStateException) {

            val headerNames = request.headerNames.toList()
            if (
                headerNames.contains("username") &&
                headerNames.contains("password")
            ) {
                procUsernamePasswordAuth(request,
                    response,
                    request.getHeader("username"),
                    request.getHeader("password")
                )
            } else {
                clearContext()
            }
        }
        filter.doFilter(request, response)
    }

    private fun procUsernamePasswordAuth(
        request: HttpServletRequest,
        httpResponse: HttpServletResponse,
        username: String,
        password: String
    ) {
         val resultOfAuthentication: Authentication =
             usernamePasswordAuth(username, password)

         request.session.setAttribute("token", resultOfAuthentication.credentials.toString())

         getContext().authentication = resultOfAuthentication
         httpResponse.status = SC_OK
    }

    private fun tryToAuthenticate(
        requestAuthentication: Authentication
    ): Authentication {
        val responseAuthentication: Authentication =
            authenticationProvider.authenticate(requestAuthentication)
        if (!responseAuthentication.isAuthenticated) {
            throw InternalAuthenticationServiceException(
                "Unable to authenticate Domain User for provided credentials")
        }
        return responseAuthentication
    }

  private fun usernamePasswordAuth(username: String, password: String): Authentication {
        val requestAuthentication = UsernamePasswordAuthenticationToken(username, password)
        return tryToAuthenticate(requestAuthentication)
    }
}

internal class UPAuthenticationProvider : AuthenticationProvider {

    override fun authenticate(auth: Authentication?): Authentication {

        if (auth?.principal.toString().isNotBlank() &&
            auth?.credentials.toString().isNotBlank()) {

            val authRes: Authentication =
                PreAuthenticatedAuthenticationToken(auth?.principal.toString(),
                    UUID.randomUUID())
            authRes.isAuthenticated = true
            return authRes
        }
        throw MissingDataException("No login data found")
    }

    override fun supports(p0: Class<*>?): Boolean = true
}

@ResponseStatus(NOT_FOUND)
internal class MissingDataException(message: String?) : AccessDeniedException(message)
