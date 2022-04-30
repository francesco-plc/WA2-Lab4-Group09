package it.polito.wa2.wa2lab4group09.dtos

import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import java.sql.Timestamp

data class TicketPurchasedDTO(
    var sub: Int?,
    var iat: Timestamp,
    var exp: Timestamp,
    var zid: String,
    var jws: String
)

fun TicketPurchased.toDTO(): TicketPurchasedDTO{
    return TicketPurchasedDTO(sub, iat, exp, zid, jws)
}
