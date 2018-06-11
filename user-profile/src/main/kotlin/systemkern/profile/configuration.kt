package systemkern.profile

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
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
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.bind.annotation.ResponseStatus
import java.security.AccessController.getContext
import java.util.UUID
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Configuration
@EnableWebSecurity
internal class CustomWebSecurityConfigurerAdapter(
    val pattern: String = "/user-profiles",
    val pattern1: String = "/user-profiles/",
    val pattern2: String = "/user-profiles/{\\d+}") : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(webSecurity: WebSecurity) {
        webSecurity
            .ignoring()
            .antMatchers(HttpMethod.POST, pattern)
            .antMatchers(HttpMethod.POST, "/logout")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.DELETE, pattern, pattern1,
                pattern2)
            .denyAll()

            .antMatchers(HttpMethod.PUT, pattern2)
            .authenticated()
            .antMatchers(HttpMethod.PUT, pattern, pattern1)
            .denyAll()

            .antMatchers(HttpMethod.GET, pattern2)
            .authenticated()
            .antMatchers(HttpMethod.GET, pattern, pattern1)
            .denyAll()

            .and()
            .addFilterBefore(AuthenticationFilter(UPAuthenticationProvider()),
                BasicAuthenticationFilter::class.java)
    }

    internal class AuthenticationFilter(val authenticationProvider: UPAuthenticationProvider)
        : GenericFilterBean() {

        override fun doFilter(request: ServletRequest,
                              response: ServletResponse,
                              filter: FilterChain) {

            request as HttpServletRequest
            response as HttpServletResponse
            try {

                if (!AuthenticationService.isValidToken(UUID.fromString(
                        request.getHeader("Authorization").split(" ")
                            .get(1)), request)) {

                    throw InvalidCredentials("Unauthorized: invalid Credentials")
                }

            } catch (E: IllegalStateException) {
                processUsernamePasswordAuthentication(request,
                    response,
                    request.getHeader("username"),
                    request.getHeader("password"))
            }
            filter.doFilter(request, response)
        }

        private fun processUsernamePasswordAuthentication(
            request: HttpServletRequest,
            httpResponse: HttpServletResponse, username: String,
            password: String) {

            val resultOfAuthentication: Authentication =
                authenticateUsernameAndPassword(username, password)

            val sess: HttpSession = request.session
            sess.setAttribute("token", resultOfAuthentication.credentials.toString())

            SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication)
            httpResponse.setStatus(HttpServletResponse.SC_OK)
        }

        private fun tryToAuthenticate(requestAuthentication: Authentication): Authentication {
            val responseAuthentication: Authentication =
                authenticationProvider.authenticate(requestAuthentication)
            if (!responseAuthentication.isAuthenticated()) {
                throw InternalAuthenticationServiceException(
                    "Unable to authenticate Domain User for provided credentials")
            }
            return responseAuthentication
        }

        private fun authenticateUsernameAndPassword(username: String,
                                                    password: String
        ): Authentication {
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
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
internal class MissingDataException(message: String?) : RuntimeException(message)

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
internal class InvalidCredentials(message: String?) : RuntimeException(message)
