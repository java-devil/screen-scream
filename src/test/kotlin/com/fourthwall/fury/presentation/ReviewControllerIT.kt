package com.fourthwall.fury.presentation

import com.fourthwall.fury.PersistenceConfiguration
import com.fourthwall.fury.core.ImdbID
import com.fourthwall.fury.core.ReviewBook
import com.fourthwall.fury.core.UserName
import com.fourthwall.fury.core.UserScore
import nu.studer.sample.tables.MovieReviews.MOVIE_REVIEWS
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import kotlin.test.assertEquals

@Import(PersistenceConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewControllerIT @Autowired constructor(val db: DSLContext, val reviewBook: ReviewBook) {

    @LocalServerPort
    private var port: Int = 0
    private val webBrowser: RestClient = RestClient.builder()
        .defaultStatusHandler({ it.is4xxClientError }) { _, response -> println(response.body) }.build()

    private val userA = "Wiesio"
    private val userB = "Miecio"
    private val userC = "Zbysio"
    private val movieA = "tt0232500"
    private val movieB = "tt0322259"

    @BeforeEach
    fun before() {
        db.deleteFrom(MOVIE_REVIEWS).execute()
        reviewBook.upsert(ImdbID(movieA), UserName(userA), UserScore(BigDecimal("5.0")))
        reviewBook.upsert(ImdbID(movieA), UserName(userB), UserScore(BigDecimal("3.5")))
        reviewBook.upsert(ImdbID(movieA), UserName(userC), UserScore(BigDecimal("1.5")))
    }

    @Test
    fun `should respond to a valid reviewed FnF IMDB ID with a mean of all User Scores in precisely the form #,#`() {
        // GIVEN:
        val url = "http://localhost:$port/api/v1/movies/$movieA/reviews"

        // WHEN:
        val response = webBrowser.get().uri(url).retrieve().toEntity(BigDecimal::class.java)

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(BigDecimal("3.3"), response.body)
    }

    @Test
    fun `should respond to a valid unreviewed FnF IMDB ID with HTTP error code 404`() {
        // GIVEN:
        val url = "http://localhost:$port/api/v1/movies/$movieB/reviews"

        // WHEN:
        val response = webBrowser.get().uri(url).retrieve().toEntity(BigDecimal::class.java)

        // THEN:
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should respond to a valid FnF IMDB ID reviewed by a User with the proper Score in precisely the form #,#`() {
        // GIVEN:
        val url = "http://localhost:$port/api/v1/movies/$movieA/reviews/$userA"

        // WHEN:
        val response = webBrowser.get().uri(url).retrieve().toEntity(UserReviewDTO::class.java)

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(BigDecimal("5.0"), response.body?.userScore)
    }

    @Test
    fun `should respond to a valid FnF IMDB ID unreviewed by a User with HTTP error code 404`() {
        // GIVEN:
        val user = "amadeusz"
        val url = "http://localhost:$port/api/v1/movies/$movieA/reviews/$user"

        // WHEN:
        val response = webBrowser.get().uri(url).retrieve().toEntity(UserReviewDTO::class.java)

        // THEN:
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should respond to a valid User Review by persisting it in the DB if a Score for the specified User does not exist`() {
        // GIVEN:
        val request = UserReviewDTO(BigDecimal("5.0"))
        val url = "http://localhost:$port/api/v1/movies/$movieB/reviews/$userA"

        // WHEN:
        val response = webBrowser.put().uri(url).body(request).retrieve().toBodilessEntity()
        val doubleCheck = reviewBook.findBy(ImdbID(movieB), UserName(userA))?.value

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(BigDecimal("5.0"), doubleCheck)
    }

    @Test
    fun `should respond to a valid User Review by overriding it in the DB if a Score for the specified User exists`() {
        // GIVEN:
        val request = UserReviewDTO(BigDecimal("1.0"))
        val url = "http://localhost:$port/api/v1/movies/$movieA/reviews/$userB"

        // WHEN:
        val response = webBrowser.put().uri(url).body(request).retrieve().toBodilessEntity()
        val doubleCheck = reviewBook.findBy(ImdbID(movieA), UserName(userB))?.value

        // THEN:
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(BigDecimal("1.0"), doubleCheck)
    }

    @Test
    fun `should respond to an invalid User Review with HTTP error code 422`() {
        // GIVEN:
        val requests = listOf(
            UserReviewDTO(BigDecimal("0.5")),
            UserReviewDTO(BigDecimal("5.1")),
            UserReviewDTO(BigDecimal("2.51"))
        )
        val url = "http://localhost:$port/api/v1/movies/$movieA/reviews/$userA"

        // WHEN:
        val responses = requests.map {
            webBrowser.put().uri(url).body(it).retrieve().toBodilessEntity()
        }

        // THEN:
        responses.forEach { assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, it.statusCode) }
    }
}
