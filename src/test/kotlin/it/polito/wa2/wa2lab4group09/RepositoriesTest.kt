package it.polito.wa2.wa2lab4group09


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.services.unwrap
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
class RepositoriesTest {

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    final var keyTicket = "questachievavieneutilizzataperfirmareiticketsLab4"

    private final val userDetailsEntity = UserDetails(
        "nameTest",
        "surnameTest",
        "addressTest",
        LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        "1234567890"
    )

    val ticketPurchasedEntity = TicketPurchased(
        Timestamp(System.currentTimeMillis()),
        Timestamp(System.currentTimeMillis()+ 3600000),
        "ABC",
        Jwts.builder()
            .setSubject(userDetailsEntity.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact()
    )

    @BeforeEach
    @Test
    fun createUserDetailsAndTicketPurchased(){
        userDetailsRepository.save(userDetailsEntity)
        userDetailsEntity.addTicket(ticketPurchasedEntity)

        val userDetailsFound = userDetailsRepository.findById(userDetailsEntity.username).unwrap()!!.toDTO()
        val countTicketPurchased = userDetailsEntity.tickets.count()

        assertEquals(userDetailsEntity.toDTO(), userDetailsFound)
        assertEquals(1, countTicketPurchased)
    }

    @Test
    fun userDetailsExist(){
        val userDetailsActual = userDetailsRepository.existsById(userDetailsEntity.username)

        assertEquals(true, userDetailsActual)
    }

    @Test
    @Transactional
    fun updateUserDetails(){
        val entity = userDetailsRepository.findAll()

        userDetailsRepository.updateUserDetails(
            "newName",
            "newSurname",
            "newAddress",
            LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            "1111111111",
            entity.last().username
        )

        val userDetailsFound = userDetailsRepository.existsById(entity.last().username)
        assertEquals( true, userDetailsFound)
    }


    @AfterEach
    @Test
    fun deleteUserDetails(){
        userDetailsRepository.delete(userDetailsEntity)

        val userDetailsFound = userDetailsRepository.existsById(userDetailsEntity.username)
        assertEquals( false, userDetailsFound)
    }

}


