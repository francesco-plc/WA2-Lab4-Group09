package it.polito.wa2.wa2lab4group09.dtos

import it.polito.wa2.wa2lab4group09.entities.Role
import it.polito.wa2.wa2lab4group09.entities.UserDetails

data class UserDetailsDTO(
    var username: String,
    var name: String? = null,
    var surname: String? = null,
    var address: String?= null,
    var date_of_birth: String? = null,
    var telephone_number: String? = null,
    var role: Role
)

fun UserDetails.toDTO(): UserDetailsDTO{
    return UserDetailsDTO(username, name, surname, address, date_of_birth, telephone_number, role)
}
