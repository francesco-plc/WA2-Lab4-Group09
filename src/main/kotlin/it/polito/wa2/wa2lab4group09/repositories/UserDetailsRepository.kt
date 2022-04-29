package it.polito.wa2.wa2lab4group09.repositories

import it.polito.wa2.wa2lab4group09.entities.UserDetails
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDetailsRepository: CrudRepository<UserDetails, String> {

    @Modifying
    @Query("update UserDetails set telephone_number = :new_number where username = :username")
    fun updateTelephone(new_number: Int, username : String)

}