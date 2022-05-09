package it.polito.wa2.wa2lab4group09.security

import it.polito.wa2.wa2lab4group09.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig() : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var appProperties:AppProperties

    override fun configure(http: HttpSecurity) {
        val authorizationFilter = JWTAuthorizationFilter(authenticationManager(), appProperties.jwtHeader, appProperties.jwtHeaderStart, appProperties.key)
        http
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no sessions
            .and()
            .addFilter(authorizationFilter)
            .antMatcher("/**")
            .authorizeRequests()
            .anyRequest().authenticated()
    }
}