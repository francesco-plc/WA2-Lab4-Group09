package it.polito.wa2.wa2lab4group09

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.controllers.ActionTicket
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.services.UserDetailsService
import it.polito.wa2.wa2lab4group09.services.unwrap
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
class ServiceTest {
/*
    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    private final var _keyTicket = "questachievavieneutilizzataperfirmareiticketsLab4"

    private final var _keyUser = "laboratorio4webapplications2ProfessorGiovanniMalnati"

    private final val userDetailsEntity = UserDetails(
        "usernameTest",
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
            .signWith(Keys.hmacShaKeyFor(_keyTicket.toByteArray())).compact()
    )


    fun generateUserToken(key: String, sub: String? = userDetailsEntity.username, exp: Date? = Date.from(Instant.now().plus(1, ChronoUnit.HOURS))): String{
        return Jwts.builder()
            .setSubject(sub)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("role", Role.CUSTOMER)
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
    }

    @BeforeEach
    @Test
    fun createUserDetails(){
        userDetailsRepository.save(userDetailsEntity)
        userDetailsEntity.addTicket(ticketPurchasedEntity)

        val userDetailsFound = userDetailsRepository.findById(userDetailsEntity.username).unwrap()!!.toDTO()
        val countTicketPurchased = userDetailsEntity.tickets.count()

        assertEquals(userDetailsEntity.toDTO(), userDetailsFound)
        assertEquals(1, countTicketPurchased)
    }

    @Test
    fun getUserDetailsValidToken(){
        val userDetailsDTO: UserDetailsDTO = userDetailsService.getUserDetails(generateUserToken(_keyUser))

        assertEquals(userDetailsEntity.toDTO(), userDetailsDTO)
    }

    @Test
    fun getUserDetailsInvalidTokenSignature(){

        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.getUserDetails(generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"))
        }
        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.message.toString())
    }

    @Test
    fun getUserDetailsInvalidExpiredToken(){
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.getUserDetails(generateUserToken(_keyUser, exp = Date.from(Instant.now().minus(1, ChronoUnit.HOURS))))
        }

    }

    @Test
    fun updateExistingUserDetailsValidToken(){
        val updatedUserDetailsDTO = UserDetailsDTO(
            "usernameTest",
            "updatedNameTest",
            "updatedSurnameTest",
            "updatedAddressTest",
            LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            "1111111111"
        )
        userDetailsService.updateUserDetails(generateUserToken(_keyUser), updatedUserDetailsDTO)

        val userDetailFound = userDetailsRepository.findById(userDetailsEntity.username).unwrap()!!.toDTO()
        assertEquals(updatedUserDetailsDTO, userDetailFound)
    }

    @Test
    fun updateNewUserDetailsValidToken(){
        val updatedUserDetailsDTO = UserDetailsDTO(
            "usernameTest",
            "updatedNameTest",
            "updatedSurnameTest",
            "updatedAddressTest",
            LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            "1111111111"
        )
        userDetailsService.updateUserDetails(generateUserToken(_keyUser, sub = updatedUserDetailsDTO.username), updatedUserDetailsDTO)

        val userDetailFound = userDetailsRepository.findById(updatedUserDetailsDTO.username).unwrap()!!.toDTO()
        assertEquals(updatedUserDetailsDTO, userDetailFound)
    }

    @Test
    fun updateUserDetailsInvalidTokenSignature(){
        val updatedUserDetailsDTO = UserDetailsDTO(
            "usernameTest",
            "updatedNameTest",
            "updatedSurnameTest",
            "updatedAddressTest",
            LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            "1111111111"
        )
        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.updateUserDetails(generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"), updatedUserDetailsDTO)
        }
        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.message.toString())

    }

    @Test
    fun updateUserDetailsInvalidExpiredToken(){
        val updatedUserDetailsDTO = UserDetailsDTO(
            "usernameTest",
            "updatedNameTest",
            "updatedSurnameTest",
            "updatedAddressTest",
            LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            "1111111111"
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.updateUserDetails(generateUserToken(_keyUser, exp = Date.from(Instant.now().minus(1, ChronoUnit.HOURS))), updatedUserDetailsDTO)
        }

    }

/*    todo -> fix fun "addTicket()" in  UserDetails: since it returns the same ticket added,
              the sub field we get is null instead we need it to be the correct id */
//    @Test
//    fun getUserTicketsValidToken(){
//        val expectedTickets = userDetailsEntity.tickets.map { it.toDTO() }
//        val actualTicket = userDetailsService.getUserTickets(generateUserToken(_keyUser))
//        assertEquals(expectedTickets, actualTicket)
//    }

    @Test
    fun getUserTicketsInvalidTokenSignature(){
        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.getUserTickets(generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"))
        }
        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.message.toString())
    }

    @Test
    fun getUserTicketsInvalidExpiredToken(){

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.getUserTickets(generateUserToken(_keyUser, exp = Date.from(Instant.now().minus(1, ChronoUnit.HOURS))))
        }
    }

/*    todo -> fix fun "addTicket()" in  UserDetails: since it returns the same ticket added,
              the sub field we get is null instead we need it to be the correct id */
//    @Test
//    fun buyTicketValidTokenAndCommand(){
//
//        val actualTickets = userDetailsService.buyTickets(
//            generateUserToken(_keyUser),
//            ActionTicket("buy_tickets", 3, "ABC")
//        )
//
//        val expectedTickets = ticketPurchasedRepository.findByUserDetails(userDetailsEntity).map{it.toDTO()}
//        assertEquals(expectedTickets, actualTickets)
//    }

    @Test
    fun buyTicketInvalidCommand(){

        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.buyTickets(
                generateUserToken(_keyUser),
                ActionTicket("ThisIsAnInvalidCommand", 3, "ABC")
            )
        }
        assertEquals("action is not supported", exception.message.toString())
    }

    @Test
    fun buyTicketInvalidTokenSignature(){

        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.buyTickets(
                generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"),
                ActionTicket("buy_tickets", 3, "ABC")
            )
        }
        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.message.toString())
    }

    @Test
    fun buyTicketsInvalidExpiredToken(){

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.buyTickets(
                generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"),
                ActionTicket("buy_tickets", 3, "ABC")
            )
        }
    }


    @AfterEach
    @Test
    fun deleteUserDetails(){
        ticketPurchasedRepository.deleteAllByUserDetails(userDetailsEntity)
        userDetailsRepository.delete(userDetailsEntity)

        val userDetailsFound = userDetailsRepository.existsById(userDetailsEntity.username)
        assertEquals( false, userDetailsFound)
    }

 */

}


enum class Role{
    CUSTOMER, ADMIN
}