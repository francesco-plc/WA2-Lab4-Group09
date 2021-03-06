package it.polito.wa2.wa2lab4group09.services

import it.polito.wa2.wa2lab4group09.dtos.TicketPurchasedDTO
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.security.JwtUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AdminService(val userDetailsRepository: UserDetailsRepository, val ticketPurchasedRepository: TicketPurchasedRepository){
    @Value("\${application.jwt.jwtSecret}")
    lateinit var key: String

    fun getTravelers(jwt:String): List<String> {
        val travelers = mutableListOf<String>()
        userDetailsRepository.getUsers().forEach { travelers.add(it) }
        return travelers
    }

    fun getTravelerProfile(jwt:String, userID:String): UserDetailsDTO {
        val userDetail = userDetailsRepository.findById(userID).unwrap()
        if (userDetail == null)
            throw IllegalArgumentException("User doesn't exist!")
        else
            return userDetail.toDTO()
    }

    fun getTravelerTickets(jwt:String, userID:String): List<TicketPurchasedDTO> {
        val tickets = mutableListOf<TicketPurchasedDTO>()
        val userDetail = userDetailsRepository.findById(userID).unwrap()
        if (userDetail == null)
            throw IllegalArgumentException("User doesn't exist!")
        else{
            ticketPurchasedRepository.findByUserDetails(userDetail).forEach {
                tickets.add(TicketPurchasedDTO(it.sub, it.iat, it.exp, it.zid, it.jws))
            }
            return tickets
        }
    }


}