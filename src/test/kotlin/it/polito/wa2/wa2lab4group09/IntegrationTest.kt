package it.polito.wa2.wa2lab4group09

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.wa2lab4group09.controllers.ActionTicket
import it.polito.wa2.wa2lab4group09.controllers.UserDetailsUpdate
import it.polito.wa2.wa2lab4group09.dtos.UserDetailsDTO
import it.polito.wa2.wa2lab4group09.entities.Role
import it.polito.wa2.wa2lab4group09.entities.TicketPurchased
import it.polito.wa2.wa2lab4group09.entities.UserDetails
import it.polito.wa2.wa2lab4group09.repositories.TicketPurchasedRepository
import it.polito.wa2.wa2lab4group09.repositories.UserDetailsRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IntegrationTest {
    companion object {

        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }

        }
    }

    @LocalServerPort
    protected final var  port = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    private final var _keyUser = "laboratorio4webapplications2ProfessorGiovanniMalnati"

    private final var _keyTicket = "questachievavieneutilizzataperfirmareiticketsLab4"

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

    private final val adminEntity = UserDetails(
        "adminUsernameTest",
        "adminNameTest",
        "adminSurnameTest",
        "adminAddressTest",
        LocalDate.of(1990,12,12).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        "1234567890",
        Role.ADMIN
    )

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
    fun getUserDetailsValid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/my/profile", HttpMethod.GET, requestEntity, Any::class.java, UserDetailsDTO::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getUserDetailsInvalid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken("129837y918273918273198723198731982739182739128273197")
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/my/profile", HttpMethod.GET, requestEntity, Any::class.java, UserDetailsDTO::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun updateUserDetailsValid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<UserDetailsUpdate>(
            UserDetailsUpdate("test", "test","test", "12-12-2022","1234567890"),
            headers
        )
        val response = restTemplate.exchange(
            "http://localhost:$port/my/profile", HttpMethod.PUT, requestEntity, Any::class.java, Unit::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun updateUserDetailsInvalid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken("129837y918273918273198723198731982739182739128273197")
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<UserDetailsUpdate>(
            UserDetailsUpdate("test", "test","test", "12-12-2022","1234567890"),
            headers
        )
        val response = restTemplate.exchange(
            "http://localhost:$port/my/profile", HttpMethod.PUT, requestEntity, Any::class.java, Unit::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun buyTicketsValid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<ActionTicket>(
            ActionTicket("buy_tickets", 3, "ABC"),
            headers
        )
        val response = restTemplate.exchange(
            "http://localhost:$port/my/tickets", HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun buyTicketsInvalidToken() {
        val headers = HttpHeaders()
        val tkn = generateUserToken("129837y918273918273198723198731982739182739128273197")
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<ActionTicket>(
            ActionTicket("buy_tickets", 3, "ABC"),
            headers
        )
        val response = restTemplate.exchange(
            "http://localhost:$port/my/tickets", HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun buyTicketsInvalidCommand() {
        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<ActionTicket>(
            ActionTicket("invalid_command", 3, "ABC"),
            headers
        )
        val response = restTemplate.exchange(
            "http://localhost:$port/my/tickets", HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun getUserTicketsValid() {

        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/my/tickets", HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getUserTicketsInvalid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken("129837y918273918273198723198731982739182739128273197")
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/my/tickets", HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getTravelersValid() {
        val headers = HttpHeaders()
        val tkn = generateAdminToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/admin/travelers", HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getTravelersInvalid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/admin/travelers", HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getTravelerProfileValid() {
        val headers = HttpHeaders()
        val tkn = generateAdminToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url: String = "http://localhost:$port/admin/traveler/${userDetailsEntity.username}/profile"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getTravelerProfileInvalid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url: String = "http://localhost:$port/admin/traveler/${userDetailsEntity.username}/profile"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getTravelerTicketsValid() {
        val headers = HttpHeaders()
        val tkn = generateAdminToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url: String = "http://localhost:$port/admin/traveler/${userDetailsEntity.username}/tickets"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getTravelerTicketsInvalid() {
        val headers = HttpHeaders()
        val tkn = generateUserToken(_keyUser)
        headers.set("Authorization", "Bearer$tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url: String = "http://localhost:$port/admin/traveler/${userDetailsEntity.username}/tickets"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

}