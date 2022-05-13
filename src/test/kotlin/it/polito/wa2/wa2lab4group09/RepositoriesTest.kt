package it.polito.wa2.wa2lab4group09


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.entities.Role
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
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
    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    private final var keyTicket = "questachievavieneutilizzataperfirmareiticketsLab4"

    private final val userDetailsEntity = UserDetails(
        "usernameTest",
        "nameTest",
        "surnameTest",
        "addressTest",
        LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        "1234567890",
        Role.CUSTOMER
    )

    val ticketPurchasedEntity = TicketPurchased(
        iat = Timestamp(System.currentTimeMillis()),
        exp = Timestamp(System.currentTimeMillis() + 3600000),
        zid = "ABC",
        jws = Jwts.builder()
            .setSubject(userDetailsEntity.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact(),
        userDetails = userDetailsEntity
    )

    @BeforeEach
    fun createUserDetailsAndTicketPurchased(){
        userDetailsRepository.save(userDetailsEntity)
        ticketPurchasedRepository.save(ticketPurchasedEntity)
    }

    @Test
    fun userDetailsExist(){
        val userDetailsFound = userDetailsRepository.findById(userDetailsEntity.username).unwrap()!!.toDTO()
        val countTicketPurchased = ticketPurchasedRepository.findByUserDetails(userDetailsEntity).count()

        assertEquals(userDetailsEntity.toDTO(), userDetailsFound)
        assertEquals(1, countTicketPurchased)
    }

    @Test
    @Transactional
    fun updateUserDetails(){
        userDetailsRepository.updateUserDetails(
            newName = "newName",
            newSurname = "newSurname",
            newAddress = "newAddress",
            newDate_of_birth = LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            new_number = "1111111111",
            username = "usernameTest"
        )

        val userDetailsFound = userDetailsRepository.findById(userDetailsEntity.username).unwrap()!!
        assertEquals( "newName", userDetailsFound.name)
        assertEquals( "newSurname", userDetailsFound.surname)
        assertEquals( "newAddress", userDetailsFound.address)
        assertEquals("12-12-1990", userDetailsFound.date_of_birth)
        assertEquals( "1111111111", userDetailsFound.telephone_number)
    }


    @AfterEach
    @Test
    fun deleteUserDetails(){
        ticketPurchasedRepository.deleteAllByUserDetails(userDetailsEntity)
        userDetailsRepository.delete(userDetailsEntity)

        val userDetailsFound = userDetailsRepository.existsById(userDetailsEntity.username)
        assertEquals( false, userDetailsFound)
    }


}


