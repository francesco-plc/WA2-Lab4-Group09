package it.polito.wa2.wa2lab4group09.controllers

import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.security.JwtUtils
import it.polito.wa2.wa2lab4group09.services.UserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController(val userDetailsService: UserDetailsService) {

    @GetMapping("/my/profile")
    fun getUserDetails(@RequestHeader("application.jwt.jwtHeader") jwt:String) : ResponseEntity<Any>{
        return if(JwtUtils.validateJwtToken(jwt)){
            ResponseEntity(userDetailsService.getUserDetails(jwt),HttpStatus.OK)
        } else ResponseEntity("Invalid Token", HttpStatus.BAD_REQUEST)
    }
}