package it.polito.wa2.wa2lab4group09.entities

import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name="ticket_purchased")
class TicketPurchased (
    @Id
    var sub: UUID = UUID.randomUUID(),
    var iat: Timestamp,
    var exp: Timestamp,
    var zid: String,
    var jws: String,
    @ManyToOne
    @JoinColumn(name = "user_details_username")
    var userDetails: UserDetails? = null
){

}