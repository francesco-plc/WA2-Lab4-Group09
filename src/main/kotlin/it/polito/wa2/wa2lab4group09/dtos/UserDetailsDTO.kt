package it.polito.wa2.wa2lab4group09.dtos

import it.polito.wa2.wa2lab4group09.entities.UserDetails
import java.time.LocalDate

data class UserDetailsDTO(
    var id: Long?,
    var name: String,
    var surname: String,
    var address: String,
    var date_of_birth: LocalDate,
    var telephone_number: Int,
)

fun UserDetails.toDTO(): UserDetailsDTO{
    return UserDetailsDTO(getId(), name, surname, address, date_of_birth, telephone_number)
}
