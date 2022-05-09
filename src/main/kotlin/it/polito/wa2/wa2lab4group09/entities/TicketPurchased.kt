package it.polito.wa2.wa2lab4group09.entities

import org.hibernate.annotations.GenericGenerator
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name="ticket_purchased")
class TicketPurchased (
    var iat: Timestamp,
    var exp: Timestamp,
    var zid: String,
    var jws: String,
    @ManyToOne
    @JoinColumn(name = "user_details_username")
    var userDetails: UserDetails? = null
){
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy="uuid2")
    var sub: UUID? = null
}