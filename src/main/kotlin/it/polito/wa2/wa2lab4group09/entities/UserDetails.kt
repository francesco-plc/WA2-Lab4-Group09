package it.polito.wa2.wa2lab4group09.entities

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "user_details")
class UserDetails(

    var name: String,
    var surname: String,
    var address: String,
    var date_of_birth: LocalDate,
    var telephone_number: Int,
) : EntityBase<Long>() {

    @OneToMany(mappedBy = "userDetails")
    val tickets = mutableListOf<TicketPurchased>()
}
