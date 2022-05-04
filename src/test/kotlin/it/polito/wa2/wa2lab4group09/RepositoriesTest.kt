package it.polito.wa2.wa2lab4group09


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
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


    final var keyTicket = "questachievavieneutilizzataperfirmareiticketsLab4"

    private final val userDetailsEntity = UserDetails(
        "nameTest",
        "surnameTest",
        "addressTest",
        LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        "1234567890"
    )

//    private final val token: String = Jwts.builder()
//        .setSubject(userDetailsEntity.username)
//        .setIssuedAt(Date.from(Instant.now()))
//        .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
//        .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact()

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

        val countUserDetails = userDetailsRepository.count()
        val countTicketPurchased = userDetailsEntity.tickets.count()

        assertEquals(1, countUserDetails)
        assertEquals(1, countTicketPurchased)
    }

    @Test
    fun userDetailsExist(){
        val entity = userDetailsRepository.findAll()
        val userDetailsFound = userDetailsRepository.findById(entity.first().username).unwrap()

        assertEquals(userDetailsFound!!.username, userDetailsEntity.username)
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
            entity.first().username)
    }





    @AfterEach
    @Test
    fun deleteUserDetails(){
        userDetailsRepository.delete(userDetailsEntity)

        val countUserDetails = userDetailsRepository.count()
        val countTicketPurchased = ticketPurchasedRepository.count()

        assertEquals(0, countUserDetails)
        assertEquals(0, countTicketPurchased)
    }


}



//extension function to get a Type from an Optional<Type>
fun <T> Optional<T>.unwrap(): T? = orElse(null)