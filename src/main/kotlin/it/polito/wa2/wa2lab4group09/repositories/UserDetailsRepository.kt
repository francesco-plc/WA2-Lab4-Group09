package it.polito.wa2.wa2lab4group09.repositories

import it.polito.wa2.wa2lab4group09.entities.UserDetails
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDetailsRepository: CrudRepository<UserDetails, String> {

    @Modifying
    @Query("update UserDetails set name = :newName, surname = :newSurname, address = :newAddress, date_of_birth = :newDate_of_birth, telephone_number = :new_number where username = :username")
    fun updateUserDetails(newName : String?, newSurname:String?, newAddress:String?, newDate_of_birth : String?, new_number: String?, username : String)

    @Query("select username from UserDetails")
    fun getUsers():List<String>

}
