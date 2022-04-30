package it.polito.wa2.wa2lab4group09.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import org.springframework.beans.factory.annotation.Value
import java.security.Key


object JwtUtils {

    fun validateJwtToken (authToken: String, key: String): Boolean{
        try {
            //jwt automatically control that the expiry timestamp (named “exp”) is still valid
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(key.toByteArray())).build().parseClaimsJws(authToken)
            return true;

        }catch (e : JwtException){
            throw IllegalArgumentException("${e.message}")
        }
    }


    fun getDetailsFromJwtToken (authToken: String, key : String): UserDetailsDTO{
        try {
            //jwt automatically control that the expiry timestamp (named “exp”) is still valid
            val jwt = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(key.toByteArray())).build().parseClaimsJws(authToken)
            val subject=jwt.body.subject
            //val role : Role = jwt.body["role"] as Role
            return UserDetailsDTO(subject)

        }catch (e : JwtException){
            throw IllegalArgumentException("${e.message}")
        }
    }
}

enum class Role{
    CUSTOMER,ADMIN
}