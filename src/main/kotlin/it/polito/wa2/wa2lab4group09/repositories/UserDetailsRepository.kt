package it.polito.wa2.wa2lab4group09.repositories

import it.polito.wa2.wa2lab4group09.entities.UserDetails
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDetailsRepository: CrudRepository<UserDetails, String> {

    @Modifying(clearAutomatically = true)
    @Query("update UserDetails set " +
            "name=(CASE WHEN :newName IS NULL THEN name ELSE :newName END), " +
            "surname = (CASE WHEN :newSurname IS NULL THEN surname ELSE :newSurname END), " +
            "address = (CASE WHEN :newAddress IS NULL THEN address ELSE :newAddress END), " +
            "date_of_birth = (CASE WHEN :newDate_of_birth IS NULL THEN date_of_birth ELSE :newDate_of_birth END), " +
            "telephone_number = (CASE WHEN :new_number IS NULL THEN telephone_number ELSE :new_number END) " +
            "where username = :username")
    fun updateUserDetails(newName : String?, newSurname:String?, newAddress:String?, newDate_of_birth : String?, new_number: String?, username : String)

    @Query("select username from UserDetails")
    fun getUsers():List<String>

}
