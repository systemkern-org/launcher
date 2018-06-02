package systemkern.profile

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

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
            .and()
            .addFilterBefore(TokenAccessFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}

class TokenAccessFilter : GenericFilterBean() {
    override fun doFilter(request: ServletRequest,
                          response: ServletResponse,
                          filter: FilterChain) {
        request as HttpServletRequest

        val token: String = request.getHeader("Authorization")
        filter.doFilter(request, response)
    }
}
