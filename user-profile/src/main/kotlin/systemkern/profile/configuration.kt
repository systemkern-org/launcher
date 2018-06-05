package systemkern.profile

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
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
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.core.endpoint.TokenResponse
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.web.bind.annotation.ResponseStatus
import sun.plugin2.message.GetAuthenticationMessage
import java.util.*
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class CustomWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {


    @Throws(Exception::class)
    override fun configure(webSecurity: WebSecurity) {
        webSecurity
            // If .antMatchers(Pattern) added after .ignoring so
            // the url that match in that pattern is ignored from filters
            .ignoring()
            .antMatchers(HttpMethod.POST, "/user-profiles")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.DELETE, "/user-profiles","/user-profiles/","/user-profiles/{\\d+}")
            .denyAll()
            .antMatchers(HttpMethod.PUT, "/user-profiles/{\\d+}")
            .authenticated()
            .antMatchers(HttpMethod.GET, "/user-profiles/{\\d+}")
            .authenticated()
            .antMatchers(HttpMethod.GET, "/user-profiles","/user-profiles/")
            .denyAll()
            .and()
            .addFilterBefore(TokenAccessFilter(UPTAuthenticationManager()),
                    UsernamePasswordAuthenticationFilter::class.java)
    }
}

class TokenAccessFilter(val authenticationManager: AuthenticationProvider) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest,
                          response: ServletResponse,
                          filter: FilterChain) {

        request as HttpServletRequest
        response as HttpServletResponse

        try {
            val token: String = request.getHeader("Authorization").split(" ").get(1)
            if(token.isNotBlank()){
                //processAuthenticationWithToken()
            }
        } catch (E: IllegalStateException) {

            processUsernamePasswordAuthentication(response, username = request.getHeader("username"),
                password = request.getHeader("password"))
        }


        filter.doFilter(request, response)

    }

    fun processUsernamePasswordAuthentication(httpResponse: HttpServletResponse, username: String ,
                                              password: String){
        val resultOfAuthentication: Authentication = tryToAuthenticateWithUsernameAndPassword(username, password)
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }

    private fun tryToAuthenticate(requestAuthentication:Authentication):Authentication {
        val responseAuthentication: Authentication =
            authenticationManager.authenticate(requestAuthentication)
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw InternalAuthenticationServiceException("Unable to authenticate Domain User for provided credentials")
        }
        return responseAuthentication
    }

    private fun tryToAuthenticateWithUsernameAndPassword(username: String, password: String): Authentication {
        val requestAuthentication = UsernamePasswordAuthenticationToken(username, password)
        return tryToAuthenticate(requestAuthentication)
    }
}
internal class UPTAuthenticationManager: AuthenticationProvider {

    override fun authenticate(auth: Authentication?): Authentication {

        if(auth?.principal.toString().isNotBlank() && auth?.credentials.toString().isNotBlank())
        {
            val authRes: Authentication = PreAuthenticatedAuthenticationToken(auth?.principal.toString(),UUID.randomUUID())
            authRes.isAuthenticated = true
            return authRes
        }
        throw MissingDataException("No login data found")
    }
    override fun supports(p0: Class<*>?): Boolean {
        return true
    }

}
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class MissingDataException(message: String?) : RuntimeException(message)