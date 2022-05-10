package it.polito.wa2.wa2lab4group09.repositories

import it.polito.wa2.wa2lab4group09.entities.UserDetails
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDetailsRepository: CrudRepository<UserDetails, String> {

    @Modifying
    @Query("update UserDetails set name = COALESCE(:newName, name) , surname = COALESCE(:newSurname, surname), address = COALESCE(:newAddress, address), date_of_birth = COALESCE(:newDate_of_birth, date_of_birth), telephone_number = COALESCE(:new_number, telephone_number) where username = :username")
    fun updateUserDetails(newName : String?, newSurname:String?, newAddress:String?, newDate_of_birth : String?, new_number: String?, username : String)

    @Query("select username from UserDetails")
    fun getUsers():List<String>

}
