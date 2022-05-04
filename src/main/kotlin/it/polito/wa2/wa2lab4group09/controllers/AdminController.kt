package it.polito.wa2.wa2lab4group09.controllers

import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.security.JwtUtils
import it.polito.wa2.wa2lab4group09.services.AdminService
import it.polito.wa2.wa2lab4group09.services.unwrap
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AdminController(val adminService: AdminService) {

    @GetMapping("/admin/travelers")
    fun getTravelers(@RequestHeader("Authorization") jwt:String) : ResponseEntity<Any>{
        println(jwt)
        val newToken = jwt.replace("Bearer", "")
        println(newToken)
        return try {
            val body = adminService.getTravelers(newToken)
            ResponseEntity(body, HttpStatus.OK)
        } catch (t : Throwable){
            println("${t.message}")
            ResponseEntity("${t.message}", HttpStatus.BAD_REQUEST)
        }
    }


    @GetMapping("/admin/traveler/{userID}/profile")
    fun getTravelerProfile(@PathVariable userID:String, @RequestHeader("Authorization") jwt:String) :  ResponseEntity<Any>{
        println(jwt)
        val newToken = jwt.replace("Bearer", "")
        println(newToken)
        return try {
            val body = adminService.getTravelerProfile(newToken, userID)
            ResponseEntity(body, HttpStatus.OK)
        } catch (t : Throwable){
            println("${t.message}")
            ResponseEntity("${t.message}", HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/admin/traveler/{userID}/tickets")
    fun getTravelerTickets(@PathVariable userID:String, @RequestHeader("Authorization") jwt:String) : Any{
        val newToken = jwt.replace("Bearer", "")
        return try {
            val body = adminService.getTravelerTickets(newToken, userID)
            ResponseEntity(body, HttpStatus.OK)
        } catch (t : Throwable){
            println("${t.message}")
            ResponseEntity("${t.message}", HttpStatus.BAD_REQUEST)
        }
    }

}