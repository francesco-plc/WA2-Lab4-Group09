package it.polito.wa2.wa2lab4group09.entities

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "user_details")
class UserDetails(

    @Id
    var username : String,
    var name: String? = null,
    var surname: String? = null,
    var address: String? = null,
    var date_of_birth: String? = null,
    var telephone_number: String? = null
){

    @OneToMany(mappedBy = "userDetails")
    val tickets : List<TicketPurchased> = mutableListOf()
}
