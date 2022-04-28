package it.polito.wa2.wa2lab4group09.entities

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name="ticket_purchased")
class TicketPurchased (

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var sub: Int,
    var iat: Timestamp,
    var exp: Timestamp,
    var zid: Int,
    var jws: String
)  {

    @ManyToOne
    @JoinColumn(name = "user_details_id")
    var userDetails: UserDetails? = null

}