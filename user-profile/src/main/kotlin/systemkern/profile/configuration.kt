package systemkern.profile

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class CustomWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {


    @Throws(Exception::class)
    override fun configure(webSecurity: WebSecurity) {
        webSecurity
            .ignoring()
            .antMatchers(HttpMethod.POST, "/login")
            .antMatchers(HttpMethod.POST, "/user-profiles")
    }
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.DELETE, "/user-profiles")
            .denyAll()
            .antMatchers(HttpMethod.PUT, "/user-profiles")
            .authenticated()
            .antMatchers(HttpMethod.PUT, "/user-profiles")
            .authenticated()
            .and()
            .addFilterBefore(TokenAccessFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}

class TokenAccessFilter : GenericFilterBean() {
    override fun doFilter(request: ServletRequest,
                          response: ServletResponse,
                          filter: FilterChain) {

        request as HttpServletRequest
        response as HttpServletResponse

        if(request.method.toLowerCase().equals("put")
            || request.method.toLowerCase().equals("get"))
        {
            response.setContentType("application/json;charset=UTF-8")
            val token: String = request.getHeader("Authorization")
            if(AuthenticationService.tokens.containsKey(UUID.fromString(token)))
            {
                response.status = HttpStatus.ACCEPTED.value()
                print(token)
            }else
            {
                SecurityContextHolder.getContext().authentication = null
                response.status = HttpStatus.FORBIDDEN.value()
                throw ForbiddenException("Forbidden")
            }
        }

        filter.doFilter(request, response)

    }
}
@ResponseStatus(value = HttpStatus.FORBIDDEN)
class ForbiddenException(message: String?) : RuntimeException(message)