package it.polito.wa2.wa2lab4group09.repositories

import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TicketPurchasedRepository: CrudRepository<TicketPurchased, Int> {

    fun findByUserDetails(userDetails: UserDetails) : List<TicketPurchased>

    @Transactional
    @Modifying
    @Query("DELETE FROM TicketPurchased WHERE userDetails =:userDetails")
    fun deleteAllByUserDetails(userDetails: UserDetails)
}