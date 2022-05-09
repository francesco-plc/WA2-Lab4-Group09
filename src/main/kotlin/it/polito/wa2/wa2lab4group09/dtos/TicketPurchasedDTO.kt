package it.polito.wa2.wa2lab4group09.dtos

import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import java.sql.Timestamp
import java.util.UUID

data class TicketPurchasedDTO(
    var sub: UUID?,
    var iat: Timestamp,
    var exp: Timestamp,
    var zid: String,
    var jws: String
)

fun TicketPurchased.toDTO(): TicketPurchasedDTO{
    return TicketPurchasedDTO(sub, iat, exp, zid, jws)
}
