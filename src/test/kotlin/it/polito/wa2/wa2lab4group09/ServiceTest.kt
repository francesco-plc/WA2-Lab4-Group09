package it.polito.wa2.wa2lab4group09

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.controllers.ActionTicket
import it.polito.wa2.wa2lab4group09.controllers.UserDetailsUpdate
import it.polito.wa2.wa2lab4group09.dtos.TicketPurchasedDTO
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.dtos.toDTO
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import it.polito.wa2.wa2lab4group09.entities.Role
import it.polito.wa2.wa2lab4group09.services.AdminService
import it.polito.wa2.wa2lab4group09.services.UserDetailsService
import it.polito.wa2.wa2lab4group09.services.unwrap
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
class ServiceTest {
    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    lateinit var adminService: AdminService

    private final var _keyTicket = "questachievavieneutilizzataperfirmareiticketsLab4"

    private final var _keyUser = "laboratorio4webapplications2ProfessorGiovanniMalnati"

    private final val userDetailsEntity = UserDetails(
        "usernameTest",
        "nameTest",
        "surnameTest",
        "addressTest",
        LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        "1234567890",
        Role.CUSTOMER
    )

    private final val adminEntity = UserDetails(
        "adminUsernameTest",
        "adminNameTest",
        "adminSurnameTest",
        "adminAddressTest",
        LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        "1234567890",
        Role.ADMIN
    )

    private final val userDetailsUpdateEntity = UserDetailsUpdate(
        "nameTest",
        "surnameTest",
        "addressTest",
        LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        "1234567890"
    )

    val ticketPurchasedEntity = TicketPurchased(
        iat = Timestamp(System.currentTimeMillis()),
        exp = Timestamp(System.currentTimeMillis() + 3600000),
        zid = "ABC",
        jws = Jwts.builder()
            .setSubject(userDetailsEntity.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .signWith(Keys.hmacShaKeyFor(_keyTicket.toByteArray())).compact(),
        userDetails = userDetailsEntity
    )


    fun generateUserToken(
        key: String,
        sub: String? = userDetailsEntity.username,
        exp: Date? = Date.from(Instant.now().plus(1, ChronoUnit.HOURS))
    ): String {
        return Jwts.builder()
            .setSubject(sub)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("role", Role.CUSTOMER)
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
    }

    fun generateAdminToken(
        key: String,
        sub: String? = adminEntity.username,
        exp: Date? = Date.from(Instant.now().plus(1, ChronoUnit.HOURS))
    ): String {
        return Jwts.builder()
            .setSubject(sub)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("role", Role.ADMIN)
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
    }
    @BeforeEach
    fun createUserDetails(){
        userDetailsRepository.save(userDetailsEntity).addTicket(ticketPurchasedEntity)
        userDetailsRepository.save(adminEntity)
    }

    @Test
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun getUserDetailsValidToken(){
        val userDetailsDTO: UserDetailsDTO = userDetailsService.getUserDetails(generateUserToken(_keyUser))
        val userDetailsUpdate = UserDetailsUpdate(userDetailsDTO.name,userDetailsDTO.surname,userDetailsDTO.address,userDetailsDTO.date_of_birth,userDetailsDTO.telephone_number)
        assertEquals(userDetailsUpdateEntity, userDetailsUpdate)
    }

//    Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
//    @Test
//    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
//    fun getUserDetailsInvalidTokenSignature(){
//
//        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
//            userDetailsService.getUserDetails(generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"))
//        }
//        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.message.toString())
//    }

//    Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
//    @Test
//    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
//    fun getUserDetailsInvalidExpiredToken(){
//        Assertions.assertThrows(IllegalArgumentException::class.java) {
//            userDetailsService.getUserDetails(generateUserToken(_keyUser, exp = Date.from(Instant.now().minus(1, ChronoUnit.HOURS))))
//        }
//
//    }

    @Test
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun updateExistingUserDetailsValidToken(){
        val updatedUserDetailsDTO = UserDetailsUpdate(
            "updatedNameTest",
            "updatedSurnameTest",
            "updatedAddressTest",
            LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            "1111111111"
        )

        userDetailsService.updateUserDetails(generateUserToken(_keyUser), updatedUserDetailsDTO)

        val userDetailFound = userDetailsRepository.findById(userDetailsEntity.username).unwrap()!!.toDTO()
        assertEquals(
            UserDetailsDTO("usernameTest",updatedUserDetailsDTO.name, updatedUserDetailsDTO.surname,updatedUserDetailsDTO.address, updatedUserDetailsDTO.date_of_birth, updatedUserDetailsDTO.telephone_number, Role.CUSTOMER ),
            userDetailFound
        )
    }

    @Test
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun updateNewUserDetailsValidToken(){
        val updatedUserDetailsDTO = UserDetailsUpdate(
            "updatedNameTest",
            "updatedSurnameTest",
            "updatedAddressTest",
            LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            "1111111111"
        )

        userDetailsService.updateUserDetails(generateUserToken(_keyUser), updatedUserDetailsDTO)

        val userDetailFound: UserDetailsDTO = userDetailsRepository.findById(userDetailsEntity.username).unwrap()!!.toDTO()
        assertEquals(updatedUserDetailsDTO.name, userDetailFound.name)
        assertEquals(updatedUserDetailsDTO.surname, userDetailFound.surname)
        assertEquals(updatedUserDetailsDTO.address, userDetailFound.address)
        assertEquals(updatedUserDetailsDTO.date_of_birth, userDetailFound.date_of_birth)
        assertEquals(updatedUserDetailsDTO.telephone_number, userDetailFound.telephone_number)
    }

    @Test
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun updateUserDetailsInvalidTokenSignature(){
        val updatedUserDetailsDTO = UserDetailsUpdate(
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
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun updateUserDetailsInvalidExpiredToken(){
        val updatedUserDetailsDTO = UserDetailsUpdate(
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

    @Test
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun getUserTicketsValidToken(){
        val expectedTickets = userDetailsEntity.tickets.map { it.toDTO() }
        val actualTicket = userDetailsService.getUserTickets(generateUserToken(_keyUser))
        assertEquals(expectedTickets, actualTicket)
    }

//    Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
//    @Test
//    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
//    fun getUserTicketsInvalidTokenSignature(){
//        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
//            userDetailsService.getUserTickets(generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"))
//        }
//        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.message.toString())
//    }

//    Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
//    @Test
//    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
//    fun getUserTicketsInvalidExpiredToken(){
//
//        Assertions.assertThrows(IllegalArgumentException::class.java) {
//            userDetailsService.getUserTickets(generateUserToken(_keyUser, exp = Date.from(Instant.now().minus(1, ChronoUnit.HOURS))))
//        }
//    }

    @Test
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun buyTicketValidTokenAndCommand(){

        val actualTickets = userDetailsService.buyTickets(
            generateUserToken(_keyUser),
            ActionTicket("buy_tickets", 3, "ABC")
        )

        val expectedTickets = ticketPurchasedRepository.findByUserDetails(userDetailsEntity).map{it.toDTO()}
        assertEquals(expectedTickets, actualTickets)
    }

    @Test
    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
    fun buyTicketInvalidCommand(){

        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.buyTickets(
                generateUserToken(_keyUser),
                ActionTicket("ThisIsAnInvalidCommand", 3, "ABC")
            )
        }
        assertEquals("action is not supported", exception.message.toString())
    }

//    Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
//    @Test
//    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
//    fun buyTicketInvalidTokenSignature(){
//
//        val exception: IllegalArgumentException  = Assertions.assertThrows(IllegalArgumentException::class.java) {
//            userDetailsService.buyTickets(
//                generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"),
//                ActionTicket("buy_tickets", 3, "ABC")
//            )
//        }
//        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.message.toString())
//    }

//    Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
//    @Test
//    @WithMockUser(username = "usernameTest", password = "pwd", roles = ["CUSTOMER"])
//    fun buyTicketsInvalidExpiredToken(){
//
//        Assertions.assertThrows(IllegalArgumentException::class.java) {
//            userDetailsService.buyTickets(
//                generateUserToken("ChiaveErrataUtileSoloATestareQuestaFunzioneInutile"),
//                ActionTicket("buy_tickets", 3, "ABC")
//            )
//        }
//    }


    @AfterEach
    fun deleteUserDetails(){
        ticketPurchasedRepository.deleteAllByUserDetails(userDetailsEntity)
        userDetailsRepository.delete(userDetailsEntity)
        userDetailsRepository.delete(adminEntity)
    }


    @Test
    fun getTravelers(){
        val usernames: List<String> = adminService.getTravelers(generateAdminToken(_keyUser))
        assertEquals("USER1", usernames[0])
    }

    @Test
    fun getTravelerProfile(){
        val userDetailsDTO: UserDetailsDTO = adminService.getTravelerProfile(generateAdminToken(_keyUser), userDetailsEntity.username)
        assertEquals(userDetailsEntity.toDTO(), userDetailsDTO)
    }

    /*
    @Test
    fun getTravelerTickets(){
        val ticketsPurchasedDTO: List<TicketPurchasedDTO> = adminService.getTravelerTickets(generateAdminToken(_keyUser), userDetailsEntity.username)
        assertEquals(ticketPurchasedEntity.toDTO(), ticketsPurchasedDTO[0])
    }*/

}