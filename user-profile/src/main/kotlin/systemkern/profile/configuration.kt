package systemkern.profile

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.sql.DataSource
import org.springframework.jdbc.datasource.DriverManagerDataSource

@Configuration
@EnableWebSecurity
class CustomWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/login")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/users")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .addFilter(TokenAccessFilter())
    }
}

    class TokenAccessFilter : GenericFilterBean() {
    override fun doFilter(request: ServletRequest,
                          response: ServletResponse,
                          filter: FilterChain) {
        request as HttpServletRequest


        val token:String = request.getHeader("Authorization")
        filter.doFilter(request,response)
    }


   /* fun getDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.postgresql.Driver")
        dataSource.url = "jdbc:postgresql://localhost:5432/testDB"
        dataSource.username = "postgres"
        dataSource.password = "postgres"
        return dataSource
    }*/
}
