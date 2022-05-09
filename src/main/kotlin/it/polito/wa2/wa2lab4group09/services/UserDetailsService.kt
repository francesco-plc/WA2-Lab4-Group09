package it.polito.wa2.wa2lab4group09.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.controllers.ActionTicket
import it.polito.wa2.wa2lab4group09.dtos.TicketPurchasedDTO
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.security.JwtUtils
import it.polito.wa2.wa2lab4group09.security.Role
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.transaction.Transactional

@Service
class UserDetailsService(val userDetailsRepository: UserDetailsRepository, val ticketPurchasedRepository: TicketPurchasedRepository) {

    @Value("\${application.jwt.jwtSecret}")
    lateinit var key: String

    @Value("\${application.jwt.jwtSecretTicket}")
    lateinit var keyTicket : String

    fun getUserDetails(jwt : String): UserDetailsDTO {
        //if(!JwtUtils.validateJwtToken(jwt,key)) throw IllegalArgumentException("Token is not valid or is expired")
        val authentication = SecurityContextHolder.getContext().authentication
        println(authentication.name)
        println(authentication.authorities.first().toString())
        val role = if(authentication.authorities.first().toString()=="CUSTOMER") Role.CUSTOMER else Role.ADMIN
        val userDetail = userDetailsRepository.findById(authentication.name).unwrap()
        return if(userDetail == null){
            userDetailsRepository.save(UserDetails(authentication.name, role= role))
            UserDetails(authentication.name, role= role).toDTO()
        }else
            userDetail.toDTO()
    }

    @Transactional
    fun updateUserDetails(jwt:String, userDetailsDTO: UserDetailsDTO){
        if(!userDetailsRepository.existsById(JwtUtils.getDetailsFromJwtToken(jwt,key).username)) {
            userDetailsRepository.save(UserDetails(userDetailsDTO.username, role= userDetailsDTO.role))
        }
        userDetailsRepository.updateUserDetails(
            userDetailsDTO.name,
            userDetailsDTO.surname,
            userDetailsDTO.address,
            userDetailsDTO.date_of_birth.toString(),
            userDetailsDTO.telephone_number,
            JwtUtils.getDetailsFromJwtToken(jwt, key).username
        )

    }

    fun getUserTickets(jwt:String): List<TicketPurchasedDTO> {
        val userDetailsDTO = getUserDetails(jwt)
        val tickets = mutableListOf<TicketPurchasedDTO>()
        ticketPurchasedRepository.findByUserDetails(UserDetails(userDetailsDTO.username, role= userDetailsDTO.role)).forEach {
            tickets.add(TicketPurchasedDTO(it.sub, it.iat, it.exp, it.zid, it.jws))
        }
        return tickets
    }

    fun buyTickets(jwt: String, actionTicket: ActionTicket): List<TicketPurchasedDTO> {
        val userDetailsDTO = getUserDetails(jwt)
        val tickets = mutableListOf<TicketPurchasedDTO>()
        if (actionTicket.cmd == "buy_tickets"){
            for (i in 1..actionTicket.quantity){
                val token = Jwts.builder()
                    .setSubject(userDetailsDTO.username)
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                    .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact()
                val t = UserDetails(userDetailsDTO.username, role= userDetailsDTO.role)
                    .addTicket(
                        TicketPurchased(
                            UUID.randomUUID(),
                            Timestamp(System.currentTimeMillis()),
                            Timestamp(System.currentTimeMillis() + 3600000),
                            actionTicket.zones,
                            token
                        )
                    )
                tickets.add(t.toDTO())
                ticketPurchasedRepository.save(t)
            }
        } else throw IllegalArgumentException("action is not supported")
        return tickets
    }
}


//extension function to get a Type from an Optional<Type>
fun <T> Optional<T>.unwrap(): T? = orElse(null)