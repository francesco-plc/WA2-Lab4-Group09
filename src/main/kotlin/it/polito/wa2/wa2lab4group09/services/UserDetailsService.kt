package it.polito.wa2.wa2lab4group09.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.controllers.ActionTicket
import it.polito.wa2.wa2lab4group09.controllers.UserDetailsUpdate
import it.polito.wa2.wa2lab4group09.dtos.TicketPurchasedDTO
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.Role
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.security.JwtUtils
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
        val role = if(authentication.authorities.first().toString()=="ROLE_CUSTOMER") Role.CUSTOMER else Role.ADMIN
        println(role)
        val userDetail = userDetailsRepository.findById(authentication.name).unwrap()
        return if(userDetail == null){
            userDetailsRepository.save(UserDetails(authentication.name, role= role))
            UserDetails(authentication.name, role= role).toDTO()
        }else
            userDetail.toDTO()
    }

    @Transactional
    fun updateUserDetails(jwt:String, userDetailsUpdate: UserDetailsUpdate){
        val username = JwtUtils.getDetailsFromJwtToken(jwt, key).username
        if(!userDetailsRepository.existsById(username)) {
            userDetailsRepository.save(UserDetails(username, role= JwtUtils.getDetailsFromJwtToken(jwt, key).role))
        }

        userDetailsRepository.updateUserDetails(
            userDetailsUpdate.name,
            userDetailsUpdate.surname,
            userDetailsUpdate.address,
            userDetailsUpdate.date_of_birth,
            userDetailsUpdate.telephone_number,
            username
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
                            iat = Timestamp(System.currentTimeMillis()),
                            exp = Timestamp(System.currentTimeMillis() + 3600000),
                            zid = actionTicket.zones,
                            jws = token
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