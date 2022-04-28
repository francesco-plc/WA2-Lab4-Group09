package it.polito.wa2.wa2lab4group09


import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@SpringBootTest
class RepositoriesTest {

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    val userDetailsEntity = UserDetails(
        "nameTest",
        "surnameTest",
        "addressTest",
        LocalDate.of(1990,12,12)/*.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))*/,
        1234567890,
    )

    @BeforeEach
    @Test
    fun createUserDetails(){
        userDetailsRepository.save(userDetailsEntity)

        val count = userDetailsRepository.count()
        assertEquals(count, 1)
    }

    @Test
    fun userDetailsExist(){
        val entity = userDetailsRepository.findAll()
        val userDetailsFound = userDetailsRepository.findById(entity.first().getId()!!).unwrap()

        assertEquals(userDetailsFound, userDetailsEntity)
    }

    @Test
    @Transactional
    fun updateUserDetails(){
        val entity = userDetailsRepository.findAll()

        userDetailsRepository.updateTelephone(1111111111, entity.first().getId()!!)
    }

    @AfterEach
    @Test
    fun deleteUserDetails(){
        userDetailsRepository.delete(userDetailsEntity)

        val count = userDetailsRepository.count()
        assertEquals(count, 0)
    }
}



//extension function to get a Type from an Optional<Type>
fun <T> Optional<T>.unwrap(): T? = orElse(null)