package it.polito.wa2.wa2lab4group09.services

import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.security.JwtUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class UserDetailsService(val userDetailsRepository: UserDetailsRepository) {

    @Value("\${application.jwt.jwtSecret}")
    lateinit var key: String

    fun getUserDetails(jwt : String): UserDetailsDTO {
        if(!JwtUtils.validateJwtToken(jwt,key)) throw IllegalArgumentException("Token is not valid or is expired")
        val userDetailsDTO = JwtUtils.getDetailsFromJwtToken(jwt,key)
        val userDetail = userDetailsRepository.findById(userDetailsDTO.username).unwrap()
        return if(userDetail == null){
            userDetailsRepository.save(UserDetails(userDetailsDTO.username))
            userDetailsDTO
        }else
            userDetail.toDTO()
    }

    @Transactional
    fun updateUserDetails(jwt:String, userDetailsDTO: UserDetailsDTO){
        if(!JwtUtils.validateJwtToken(jwt,key)) throw IllegalArgumentException("Token is not valid or is expired")
        if(userDetailsRepository.existsById(JwtUtils.getDetailsFromJwtToken(jwt,key).username)) {
            userDetailsRepository.updateUserDetails(
                userDetailsDTO.name,
                userDetailsDTO.surname,
                userDetailsDTO.address,
                userDetailsDTO.date_of_birth.toString(),
                userDetailsDTO.telephone_number,
                JwtUtils.getDetailsFromJwtToken(jwt, key).username
            )
        } else {
            userDetailsRepository.save(UserDetails(userDetailsDTO.username))
            userDetailsRepository.updateUserDetails(
                userDetailsDTO.name,
                userDetailsDTO.surname,
                userDetailsDTO.address,
                userDetailsDTO.date_of_birth.toString(),
                userDetailsDTO.telephone_number,
                JwtUtils.getDetailsFromJwtToken(jwt, key).username
            )
        }
    }
}


//extension function to get a Type from an Optional<Type>
fun <T> Optional<T>.unwrap(): T? = orElse(null)