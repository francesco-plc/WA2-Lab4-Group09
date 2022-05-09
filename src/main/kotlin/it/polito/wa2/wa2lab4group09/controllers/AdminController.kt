package it.polito.wa2.wa2lab4group09.controllers

import it.polito.wa2.wa2lab4group09.services.AdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class AdminController(val adminService: AdminService) {

    //@Secured("ADMIN")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Secured("ROLE_ADMIN")
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

    @Secured("ROLE_ADMIN")
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