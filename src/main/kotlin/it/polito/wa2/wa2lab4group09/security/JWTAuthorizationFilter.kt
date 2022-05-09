package it.polito.wa2.wa2lab4group09.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    authManager: AuthenticationManager,
    private val jwtHeader: String,
    private val jwtHeaderStart: String,
    private val key: String
) : BasicAuthenticationFilter(authManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = req.getHeader(jwtHeader)
        if (header == null || !header.startsWith(jwtHeaderStart)) {
            chain.doFilter(req, res)
            return
        }
        getAuthentication(header)?.also {
            SecurityContextHolder.getContext().authentication = it
        }
        chain.doFilter(req, res)
    }

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken? {
        return try {
            JwtUtils.validateJwtToken( token.replace("Bearer", ""), key)
            val userDetailsDTO = JwtUtils.getDetailsFromJwtToken(token.replace("Bearer", ""), key)
            val authorities = HashSet<GrantedAuthority>(1)
            authorities.add(SimpleGrantedAuthority("ROLE_"+userDetailsDTO.role.toString()))
            UsernamePasswordAuthenticationToken(userDetailsDTO.username, null, authorities)
        } catch (e: Exception) {
            println(e)
            return null
        }
    }
}
