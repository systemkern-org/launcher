package systemkern.profile

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class CustomWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/users", "/users/")
            .denyAll()
            .antMatchers(HttpMethod.GET, "/users/{id}")
            .authenticated()
            .antMatchers(HttpMethod.POST, "/users", "/users/")
            .anonymous()
            .antMatchers(HttpMethod.PUT, "/users/{id}")
            .authenticated()
            .antMatchers(HttpMethod.DELETE, "/users", "/users/","/users/{id}")
            .denyAll()
            .antMatchers(HttpMethod.POST, "/login", "/login/")
            .anonymous()
    }
}
