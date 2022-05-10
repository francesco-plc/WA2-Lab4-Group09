package it.polito.wa2.wa2lab4group09.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
object JwtUtils {

    fun validateJwtToken (authToken: String, key: String): Boolean{
        try {
            //jwt automatically control that the expiry timestamp (named “exp”) is still valid
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(key.toByteArray())).build().parseClaimsJws(authToken)
            return true
        }catch (e : JwtException){
            throw IllegalArgumentException("${e.message}")
        }
    }


    fun getDetailsFromJwtToken (authToken: String, key : String): UserDetailsDTO{
        try {
            //jwt automatically control that the expiry timestamp (named “exp”) is still valid
            val jwt = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(key.toByteArray())).build().parseClaimsJws(authToken)
            val subject = jwt.body.subject
            println(jwt.body["role"])
            val role = if(jwt.body["role"]=="CUSTOMER") Role.CUSTOMER else Role.ADMIN
            println(role)
            println(subject)
            return UserDetailsDTO(subject, role = role)

        }catch (e : JwtException){
            throw IllegalArgumentException("${e.message}")
        }
    }
}

enum class Role{
    CUSTOMER,ADMIN
}