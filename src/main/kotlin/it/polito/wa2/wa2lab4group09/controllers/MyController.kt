package it.polito.wa2.wa2lab4group09.controllers

import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.services.UserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController(val userDetailsService: UserDetailsService) {

    @GetMapping("/my/profile")
    fun getUserDetails(@RequestHeader("Authorization") jwt:String) : Any{
        println(jwt)
        val newToken = jwt.replace("Bearer", "")
        println(newToken)
        return try {
            userDetailsService.getUserDetails(newToken)
        } catch (t : Throwable){
            println("${t.message}")
        }
    }

    @PutMapping("/my/profile")
    fun updateUserDetails(@RequestHeader("Authorization") jwt:String, @RequestBody userDetailsDTO: UserDetailsDTO) : ResponseEntity<Unit>{
        val newToken = jwt.replace("Bearer", "")
        return try {
            userDetailsService.updateUserDetails(newToken,userDetailsDTO)
            ResponseEntity(HttpStatus.OK)
        } catch (t : Throwable){
            println("${t.message}")
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/my/tickets", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getUserTickets(@RequestHeader("Authorization") jwt:String) : ResponseEntity<Any>{
        val newToken = jwt.replace("Bearer", "")
        return try {
            val body = userDetailsService.getUserTickets(newToken)
            ResponseEntity(body, HttpStatus.OK)
        } catch (t : Throwable){
            val body = ErrorMessage(t.message)
            ResponseEntity(body, HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/my/tickets")
    fun buyTickets(@RequestHeader("Authorization") jwt:String, @RequestBody actionTicket: ActionTicket) : ResponseEntity<Any>{
        val newToken = jwt.replace("Bearer", "")
        return try {
            val body = userDetailsService.buyTickets(newToken,actionTicket)
            ResponseEntity(body, HttpStatus.OK)
        } catch (t : Throwable){
            println(t.message)
            ResponseEntity("${t.message}", HttpStatus.BAD_REQUEST)
        }
    }
}

data class ActionTicket(val cmd : String, val quantity : Int, val zones : String)
//to return a JSON-shaped error
data class ErrorMessage(val error: String?)