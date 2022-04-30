package it.polito.wa2.wa2lab4group09.repositories

import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketPurchasedRepository: CrudRepository<TicketPurchased, Int> {

    fun findByUserDetails(userDetails: UserDetails) : List<TicketPurchased>
}