package com.fourthwall.fury.presentation

import com.fourthwall.fury.core.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScheduleControllerIT @Autowired constructor(val movieSchedule: MovieSchedule) {

    @LocalServerPort
    private var port: Int = 0
    private val webBrowser: RestClient = RestClient.builder()
        .defaultStatusHandler({ it.is4xxClientError }) { _, response -> println(response.body) }.build()

    private val movieA = "tt0232500"
    private val movieB = "tt0322259"
    private val priceA = Price(BigDecimal.valueOf(16))
    private val showTimeA = LocalDateTime.of(2025, 1, 1, 15, 0)
    private val showTimeB = LocalDateTime.of(2025, 1, 1, 18, 0)
    private val showTimeC = LocalDateTime.of(2025, 1, 1, 21, 0)

    @BeforeEach
    fun before() {
        movieSchedule.findAll()
            .forEach { movieSchedule.free(it.booking) }

        movieSchedule.book(Screening(ImdbID(movieA), priceA, showTimeA))
        movieSchedule.book(Screening(ImdbID(movieB), priceA, showTimeB))
        movieSchedule.book(Screening(ImdbID(movieB), priceA, showTimeC))
    }

    @Test
    fun `should respond with a list of all Booked Screenings`() {
        // GIVEN:
        val url = "http://localhost:$port/api/v1/schedules"

        // WHEN:
        val responseType = object : ParameterizedTypeReference<Collection<BookedScreeningDTO>>() {}
        val response = webBrowser.get().uri(url).retrieve().toEntity(responseType)

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue { movieSchedule.findAll().size == 3 }
        assertTrue { response.body?.size == 3 }
        assertTrue { response.body?.count { it.imdbID == movieA } == 1 }
        assertTrue { response.body?.count { it.imdbID == movieB } == 2 }
        assertTrue { response.body?.count { it.showTime == showTimeA } == 1 }
        assertTrue { response.body?.count { it.showTime == showTimeB } == 1 }
        assertTrue { response.body?.count { it.showTime == showTimeC } == 1 }
    }

    @Test
    fun `should respond to a valid FnF IMDB ID with a list of all Booked Screenings for that specified IMDB ID`() {
        // GIVEN:
        val url = "http://localhost:$port/api/v1/schedules/$movieB"

        // WHEN:
        val responseType = object : ParameterizedTypeReference<Collection<BookedScreeningDTO>>() {}
        val response = webBrowser.get().uri(url).retrieve().toEntity(responseType)

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue { movieSchedule.findAll().size == 3 }
        assertTrue { response.body?.size == 2 }
        assertTrue { response.body?.count { it.imdbID == movieB } == 2 }
        assertTrue { response.body?.count { it.showTime == showTimeB } == 1 }
        assertTrue { response.body?.count { it.showTime == showTimeC } == 1 }
    }

    @Test
    fun `should successfully book a Screening if the specified show time is free`() {
        // GIVEN:
        val request = ScreeningDTO("tt0463985", "32.00", LocalDateTime.of(2025, 1, 2, 15, 0))
        val url = "http://localhost:$port/api/v1/schedules"

        // WHEN:
        val response = webBrowser.post().uri(url).body(request).retrieve().toEntity(UUID::class.java)
        val doubleCheck = response.body?.let { movieSchedule.findBy(it) }?.let { BookedScreeningDTO.from(it) }

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue { movieSchedule.findAll().size == 4 }
        assertEquals(request.imdbID, doubleCheck?.imdbID)
        assertEquals(request.price, doubleCheck?.price)
        assertEquals(request.showTime, doubleCheck?.showTime)
    }

    @Test
    fun `should respond with HTTP error code 409 if the specified show time is booked by another Screening`() {
        // GIVEN:
        val request = ScreeningDTO("tt0463985", "32.00", showTimeA)
        val url = "http://localhost:$port/api/v1/schedules"

        // WHEN:
        val response = webBrowser.post().uri(url).body(request).retrieve().toBodilessEntity()

        // THEN:
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertTrue { movieSchedule.findAll().size == 3 }
    }

    @Test
    fun `should successfully free the specified Movie Booking if it exists`() {
        // GIVEN:
        val booking = movieSchedule.findAll().first().booking
        val url = "http://localhost:$port/api/v1/schedules/$booking"

        // WHEN:
        val response = webBrowser.delete().uri(url).retrieve().toBodilessEntity()

        // THEN:
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertTrue { movieSchedule.findAll().size == 2 }
        assertNull(movieSchedule.findBy(booking))
    }

    @Test
    fun `should respond with HTTP error code 404 if the specified Movie Booking does not exist`() {
        // GIVEN:
        val booking = UUID.randomUUID()
        val url = "http://localhost:$port/api/v1/schedules/$booking"

        // WHEN:
        val response = webBrowser.delete().uri(url).retrieve().toBodilessEntity()

        // THEN:
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue { movieSchedule.findAll().size == 3 }
    }
}
